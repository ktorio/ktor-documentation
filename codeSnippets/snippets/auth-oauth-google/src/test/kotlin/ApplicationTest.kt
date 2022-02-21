package com.example

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testHello() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }
        val testHttpClient = createClient {
            install(HttpCookies)
            install(ContentNegotiation) {
                json()
            }
        }
        externalServices {
            hosts("https://www.googleapis.com") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
                routing {
                    get("oauth2/v2/userinfo") {
                        call.respond(UserInfo("1", "JetBrains", "", "", "", ""))
                    }
                }
            }
        }
        application {
            main(testHttpClient)
            routing {
                get("/login-test") {
                    call.sessions.set(UserSession("abc123"))
                }
            }
        }
        val loginResponse = testHttpClient.get("/login-test")
        val helloResponse = testHttpClient.get("/hello")
        assertEquals("Hello, JetBrains!", helloResponse.bodyAsText())
    }
}

package com.example

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testHello() = testApplication {
        application {
            main()
        }
        environment {
            config = ApplicationConfig("application-custom.conf")
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val loginResponse = client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(User("jetbrains", "foobar"))
        }
        val authToken = loginResponse.body<AuthToken>().token
        val helloResponseText = client.get("hello") {
            header(HttpHeaders.Authorization, "Bearer $authToken")
        }.bodyAsText()
        assertTrue {
            helloResponseText.contains("Hello, jetbrains!")
        }
    }
}

@Serializable
data class AuthToken(val token: String)

package com.example

import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testLoginProcess() = testApplication {
        val loginClient = createClient {
            install(HttpCookies)
        }

        loginClient.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("username" to "jetbrains", "password" to "foobar").formUrlEncode())
        }.let {
            assertEquals("user_session", it.setCookie()[0].name)
        }

        loginClient.get("/admin") {
            val credentials = Base64.getEncoder().encodeToString("admin:password".toByteArray())
            header(HttpHeaders.Authorization, "Basic $credentials")
        }.let {
            assertEquals("Hi, jetbrains! Welcome to the Admin page.", it.bodyAsText())
        }

        val logoutClient = createClient {
            followRedirects = false
        }
        logoutClient.get("/logout").let {
            assertEquals("/login", it.headers["Location"])
        }
    }
}

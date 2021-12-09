package com.example

import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRequests() = testApplication {
        val client = createClient {
            install(HttpCookies)
            expectSuccess = false
        }

        val loginResponse = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("username" to "jetbrains", "password" to "foobar").formUrlEncode())
        }
        assertEquals("user_session", loginResponse.setCookie()[0].name)
    }
}

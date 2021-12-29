package com.example

import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRequests() = testApplication {
        val client = createClient {
            install(HttpCookies)
        }
        val loginResponse = client.get("/login")
        assertEquals("Product IDs: [1, 3, 7]", loginResponse.bodyAsText())
        val logoutResponse = client.get("/logout")
        assertEquals("Your basket is empty.", logoutResponse.bodyAsText())
    }
}

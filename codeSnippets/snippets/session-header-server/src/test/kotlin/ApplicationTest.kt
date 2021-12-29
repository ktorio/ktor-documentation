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
        val cartSessionHeader =  loginResponse.headers["cart_session"]
        val userResponse = client.get("/cart") { header("cart_session", cartSessionHeader) }
        assertEquals("Product IDs: [1, 3, 7]", userResponse.bodyAsText())
        val logoutResponse = client.get("/logout") { header("cart_session", cartSessionHeader) }
        assertEquals("Successful logout", logoutResponse.bodyAsText())
        val userResponse2 = client.get("/cart") { header("cart_session", cartSessionHeader) }
        assertEquals("Your basket is empty.", userResponse2.bodyAsText())
    }
}

package com.example;

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testAuthRoute() = testApplication {
        application {
            module()
        }
        val client = createClient {
            defaultRequest {
                val credentials = Base64.getEncoder().encodeToString("jetbrains:foobar".toByteArray())
                header(HttpHeaders.Authorization, "Basic $credentials")
            }
        }
        val response1 = client.get("/admin")
        assertEquals("You are not allowed to visit this page", response1.bodyAsText())
        assertEquals(HttpStatusCode.Forbidden, response1.status)
        val response2 = client.get("/profile")
        assertEquals("Hello, jetbrains!", response2.bodyAsText())
        assertEquals(HttpStatusCode.OK, response2.status)
    }
}

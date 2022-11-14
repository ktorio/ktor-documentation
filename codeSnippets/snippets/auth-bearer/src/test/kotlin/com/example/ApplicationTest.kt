package com.example;

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testAuthRoute() = testApplication {
        val response = client.get("/") {
            header(HttpHeaders.Authorization, "Bearer abc123")
        }
        assertEquals("Hello, jetbrains!", response.bodyAsText())
    }
}

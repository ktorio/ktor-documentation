package com.example

import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.*

class CustomHeaderTest {
    @Test
    fun testCustomHeader() = testApplication {
        application {
            main()
        }
        val response = client.get("/")
        assertEquals("Hello, world!", response.headers["X-Custom-Header"])
    }
}

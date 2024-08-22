package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testXForwardedValue() = testApplication {
        application {
            module()
        }
        client.get("/hello") {
            header("X-Forwarded-For", "10.0.0.123, proxy-1, proxy-2, proxy-3")
        }.apply {
            assertTrue { bodyAsText().contains("10.0.0.123") }
            assertFalse { bodyAsText().contains("proxy-3") }
        }
    }
}

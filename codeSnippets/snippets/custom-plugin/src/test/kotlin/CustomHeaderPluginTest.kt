package com.example

import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.*

class CustomHeaderPluginTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals("Hello, world!", response.headers["X-Custom-Header"].toString())
    }
}

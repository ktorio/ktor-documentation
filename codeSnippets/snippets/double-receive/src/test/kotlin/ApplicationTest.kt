package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testDoubleReceive() = testApplication {
        application {
            module()
        }
        val response = client.post("/") {
            setBody("Hello, world!")
        }
        assertEquals("Text 'Hello, world!' is received", response.bodyAsText())
    }
}

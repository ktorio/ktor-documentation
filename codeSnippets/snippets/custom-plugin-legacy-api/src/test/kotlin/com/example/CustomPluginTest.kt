package com.example

import io.ktor.client.request.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class CustomPluginTest {
    @Test
    fun testCustomHeader() = testApplication {
        val response = client.get("/")
        assertEquals("World", response.headers["Hello"])
    }
}

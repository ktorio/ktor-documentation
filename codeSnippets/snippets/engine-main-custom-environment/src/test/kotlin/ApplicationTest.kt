package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRootDev() = testApplication {
        System.setProperty("KTOR_ENV", "dev")
        client.get("/").let {
            assertEquals("Development", it.bodyAsText())
        }
    }
}

package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testDevEnvironment() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "dev")
        }
        application {
            module()
        }
        client.get("/").let {
            assertEquals("Development", it.bodyAsText())
        }
    }
}

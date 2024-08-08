package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testMetrics() = testApplication {
        application {
            module()
        }
        client.get("/metrics").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue {
                response.bodyAsText().contains("ktor_http_server_requests_active")
            }
        }
    }
}

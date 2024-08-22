package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRateLimits() = testApplication {
        application {
            module()
        }
        repeat(5) {
            client.get("/").let {
                assertEquals(HttpStatusCode.OK, it.status)
            }
        }
        client.get("/").let {
            assertEquals(HttpStatusCode.TooManyRequests, it.status)
        }

        repeat(10) {
            client.get("/public-api").let {
                assertEquals(HttpStatusCode.OK, it.status)
            }
        }
        client.get("/public-api").let {
            assertEquals(HttpStatusCode.TooManyRequests, it.status)
        }

        client.get("/protected-api?login=jetbrains").let {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }
}

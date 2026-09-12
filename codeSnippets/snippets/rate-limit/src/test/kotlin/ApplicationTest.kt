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

        repeat(5) {
            val response = client.get("/ip-api")
            assertEquals(HttpStatusCode.OK, response.status)
        }
        client.get("/ip-api").let {
            assertEquals(HttpStatusCode.TooManyRequests, it.status)
        }

        repeat(5) {
            val response = client.get("/keyed-api") {
                header("X-Api-Key", "demo-key")
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }
        client.get("/keyed-api") {
            header("X-Api-Key", "demo-key")
        }.let {
            assertEquals(HttpStatusCode.TooManyRequests, it.status)
        }

        client.get("/keyed-api") {
            header("X-Api-Key", "other-key")
        }.let {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }
}

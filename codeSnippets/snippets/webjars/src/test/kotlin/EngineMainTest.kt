package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testIndex() = testApplication {
        application {
            module()
        }
        val response = client.get("/static/index.html")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

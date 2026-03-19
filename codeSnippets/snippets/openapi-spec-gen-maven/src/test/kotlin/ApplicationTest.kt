package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testMessages() = testApplication {
        application {
            module()
        }
        client.get("/api/messages").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}
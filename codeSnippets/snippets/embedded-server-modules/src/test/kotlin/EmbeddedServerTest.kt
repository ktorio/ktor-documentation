package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/module")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from 'module'!", response.bodyAsText())
    }
}
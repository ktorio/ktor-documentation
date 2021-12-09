package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testModule1() = testApplication {
        val response = client.get("/module1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from 'module1'!", response.bodyAsText())
    }
    @Test
    fun testModule2() = testApplication {
        val response = client.get("/module2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from 'module2'!", response.bodyAsText())
    }
    @Test
    fun testModule3() = testApplication {
        val response = client.get("/module3")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from 'module3'!", response.bodyAsText())
    }
}

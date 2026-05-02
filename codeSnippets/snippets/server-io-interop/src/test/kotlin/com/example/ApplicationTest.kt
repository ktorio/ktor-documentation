package com.example

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testSink() = testApplication {
        application { module() }
        val response = client.get("/sink")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from kotlinx-io Sink!", response.bodyAsText())
    }

    @Test
    fun testRawSink() = testApplication {
        application { module() }
        val response = client.get("/raw-sink")
        assertEquals(HttpStatusCode.OK, response.status)
        val expected = byteArrayOf(42) + "Hello via RawSink".toByteArray()
        assertContentEquals(expected, response.body())
    }

    @Test
    fun testOutputStream() = testApplication {
        application { module() }
        val response = client.get("/output-stream")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello from OutputStream-backed channel!", response.bodyAsText())
    }
}

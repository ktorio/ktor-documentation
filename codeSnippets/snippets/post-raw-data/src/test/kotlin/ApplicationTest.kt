package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testPost() = testApplication {
        val response = client.post("/text") {
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("Hello, world!")
        }
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testPostChannel() = testApplication {
        val response = client.post("/channel") {
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("Hello, world!")
        }
        assertEquals("Hello, world!", response.bodyAsText())
    }
}

package com.example

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testStringValidation () = testApplication {
        val response = client.post("/text") {
            contentType(ContentType.Text.Plain)
            setBody("Some text")
        }
        assertEquals("Body text should start with 'Hello'", response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testJsonValidation() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/json") {
            contentType(ContentType.Application.Json)
            setBody(Customer(-1, "Jet", "Brains"))
        }
        assertEquals("A customer ID should be greater than 0", response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testByteArrayValidation () = testApplication {
        val response = client.post("/array") {
            contentType(ContentType.Text.Plain)
            setBody("-1")
        }
        assertEquals("A value should be greater than 0", response.bodyAsText())
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}

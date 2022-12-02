package com.example

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.*
import java.io.*
import kotlin.test.*
import kotlin.test.Test

class CustomerTests {
    @Test
    fun testSwaggerUIStatus() = testApplication {
        val response = client.get("/swagger")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testOpenApiStatus() = testApplication {
        val response = client.get("/openapi")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetCustomer() = testApplication {
        val response = client.get("/customer/1")
        assertEquals(
            """
                {
                    "id": 1,
                    "firstName": "Jane",
                    "lastName": "Smith"
                }
            """.trimIndent(),
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostCustomer() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(Customer(3, "Jet", "Brains"))
        }
        assertEquals("Customer stored correctly", response.bodyAsText())
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @After
    fun deleteDocsDir() {
        File("docs").deleteRecursively()
    }
}

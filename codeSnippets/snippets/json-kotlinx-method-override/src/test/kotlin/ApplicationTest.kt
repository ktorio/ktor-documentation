package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class CustomerTests {
    @Test
    fun testDeleteCustomer() = testApplication {
        application {
            main()
        }
        val response = client.post("/customer/3") {
            header(HttpHeaders.XHttpMethodOverride, "DELETE")
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
    }
}

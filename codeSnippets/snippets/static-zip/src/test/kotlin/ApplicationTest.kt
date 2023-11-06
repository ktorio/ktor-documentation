package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testFiles() = testApplication {
        val response = client.get("/files")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

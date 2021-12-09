package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class AuthHeadTest {
    @Test
    fun respondsSuccessfullyForHeadRequest() = testApplication {
        val headResponse = client.head("/home")
        assertEquals(HttpStatusCode.OK, headResponse.status)
    }
}

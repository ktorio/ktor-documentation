package com.example

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class AuthHeadTest {
    @Test
    fun respondsSuccessfullyForHeadRequest() {
        withTestApplication {
            application.main()
            assertEquals(HttpStatusCode.OK, handleRequest(HttpMethod.Head, "/home").response.status())
            assertEquals(
                "This is a response to a GET, but HEAD also works",
                handleRequest(HttpMethod.Get, "/home").response.content
            )
        }
    }
}

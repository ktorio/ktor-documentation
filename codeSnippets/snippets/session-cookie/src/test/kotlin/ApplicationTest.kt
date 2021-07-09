package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRequests() = withTestApplication(Application::main) {
        fun doRequestAndCheckResponse(path: String, expected: String) {
            handleRequest(HttpMethod.Get, path).apply {
                assertEquals(expected, response.content)
            }
        }

        cookiesSession {
            handleRequest(HttpMethod.Get, "/login") {}.apply {}
            doRequestAndCheckResponse("/", "Session ID is 123abc. Reload count is 0.")
            doRequestAndCheckResponse("/", "Session ID is 123abc. Reload count is 1.")
            doRequestAndCheckResponse("/", "Session ID is 123abc. Reload count is 2.")

            handleRequest(HttpMethod.Get, "/logout").apply {}
            doRequestAndCheckResponse("/", "Session doesn't exist or is expired.")
        }
    }
}
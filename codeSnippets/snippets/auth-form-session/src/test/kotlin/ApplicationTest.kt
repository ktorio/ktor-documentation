package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.sessions.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRequests() = withTestApplication(Application::main) {
        fun doRequestAndCheckResponse(path: String, expectedContent: String) {
            handleRequest(HttpMethod.Get, path).apply {
                assertEquals(expectedContent, response.content)
            }
        }

        cookiesSession {
            with(handleRequest(HttpMethod.Post, "/login"){
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("username" to "jetbrains", "password" to "foobar").formUrlEncode())
            }) {
                assertEquals("jetbrains", sessions.get<UserSession>()!!.name)
            }

            doRequestAndCheckResponse("/hello", "Hello, jetbrains! Visit count is 1.")
            doRequestAndCheckResponse("/hello", "Hello, jetbrains! Visit count is 2.")
            doRequestAndCheckResponse("/hello", "Hello, jetbrains! Visit count is 3.")
        }
    }
}

package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRequests() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Post, "/signup"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("username" to "JetBrains", "email" to "example@jetbrains.com", "password" to "foobar", "confirmation" to "foobar").formUrlEncode())
        }) {
            assertEquals("The 'JetBrains' account is created", response.content)
        }
    }
}
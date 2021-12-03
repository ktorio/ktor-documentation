package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRedirectHttps() = testApplication {
        val response = client.get("/") {
            header(HttpHeaders.XForwardedProto, "https")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testRedirectHttpsLegacyApi() = testApplication {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/") {
                addHeader(HttpHeaders.XForwardedProto, "https")
            }.let { call ->
                assertEquals(HttpStatusCode.OK, call.response.status())
                assertEquals("Hello, world!", call.response.content)
            }
        }
    }
}

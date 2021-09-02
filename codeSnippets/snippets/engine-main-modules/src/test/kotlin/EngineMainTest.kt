package com.example

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.sample.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication(Application::module1) {
            handleRequest(HttpMethod.Get, "/module1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello from 'module1'!", response.content)
            }
        }
        withTestApplication(Application::module2) {
            handleRequest(HttpMethod.Get, "/module2").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello from 'module2'!", response.content)
            }
        }
        withTestApplication(Application::module3) {
            handleRequest(HttpMethod.Get, "/module3").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello from 'module3'!", response.content)
            }
        }
    }
}
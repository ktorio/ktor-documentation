package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testIndex() = testApplication {
        client.get("/index").let { response ->
            assertEquals("max-age=1800", response.cacheControl()[0].value)
        }
    }

    @Test
    fun testAbout() = testApplication {
        client.get("/about").let { response ->
            assertEquals("max-age=60", response.cacheControl()[0].value)
        }
    }

    @Test
    fun testProfile() = testApplication {
        client.get("/profile").let { response ->
            assertEquals("no-store", response.cacheControl()[0].value)
        }
    }
}

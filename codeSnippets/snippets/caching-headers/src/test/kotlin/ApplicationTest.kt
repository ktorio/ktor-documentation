package com.example

import cachingheaders.module
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testIndex() = testApplication {
        application {
            module()
        }
        client.get("/index").let { response ->
            assertEquals("max-age=1800", response.cacheControl()[0].value)
        }
    }

    @Test
    fun testAbout() = testApplication {
        application {
            module()
        }
        client.get("/about").let { response ->
            assertEquals("max-age=60", response.cacheControl()[0].value)
        }
    }

    @Test
    fun testProfile() = testApplication {
        application {
            module()
        }
        client.get("/profile").let { response ->
            assertEquals("no-store", response.cacheControl()[0].value)
        }
    }
}

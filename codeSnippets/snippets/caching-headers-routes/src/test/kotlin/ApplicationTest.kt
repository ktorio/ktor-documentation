package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testCachingHeaders() = testApplication {
        val indexResponse = client.get("/index")
        assertEquals("max-age=3600", indexResponse.cacheControl()[0].value)
        val profileResponse = client.get("/profile")
        assertEquals("no-store", profileResponse.cacheControl()[0].value)
        assertEquals("private", profileResponse.cacheControl()[1].value)
    }
}

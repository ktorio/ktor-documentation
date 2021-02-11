package io.ktor.application.newapi.samples

import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.Assert.assertEquals
import org.junit.Test

class TestSimpleFeature {
    @Test
    fun testHelloApplication() {
        withTestApplication {
            lateinit var state: String

            application.usageExample()

            handleRequest(HttpMethod.Get, "/money") {
                addHeader("money", "100.0") // 100$
                setBody("Hahaha")
            }.let { call ->
                val result = call.response.headers["money"] ?: ""
                assertEquals(
                    "83.0", // 100$ = 83 eur
                    result
                )
            }
        }
    }
}
package io.ktor.application.newapi.samples

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import junit.framework.Assert.assertEquals
import org.junit.Test

class SerializationTests {
    @Test
    fun testSerizlization() {
        withTestApplication {
            application.formulaModule()

            // Any prefix, not matching any `excludePrefix` will be permanently moved:
            handleRequest(HttpMethod.Get, "/formula").let { call ->
                assertEquals("cake * cake + 1", call.response.content)
            }
        }
    }
}
package io.ktor.application.newapi.samples

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import junit.framework.Assert.assertEquals
import org.junit.Test

class HttpRedirectTest {
    // This test should pass. It shows HttpsRedirect feature in action:
    @Test
    fun testRedirectHttpsExemption() {
        withTestApplication {

            application.install(HttpsRedirect) {
                // HttpStatusCode.MovedPermanently will be returned:
                permanentRedirect = true

                // Here we define a prefix to exclude from a permanent redirect
                excludePrefix("/exempted")

                // This port will be used for redirect:
                sslPort = 239
            }
            application.routing {
                get("/exempted/path") {
                    call.respond("ok")
                }
            }

            // Any prefix, not matching any `excludePrefix` will be permanently moved:
            handleRequest(HttpMethod.Get, "/nonexempted").let { call ->
                assertEquals(HttpStatusCode.MovedPermanently, call.response.status())
            }

            // But excluded prefix and it's subpaths will work:
            handleRequest(HttpMethod.Get, "/exempted/path").let { call ->
                assertEquals(HttpStatusCode.OK, call.response.status())
            }
        }
    }
}
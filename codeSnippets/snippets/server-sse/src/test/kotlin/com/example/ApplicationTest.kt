package com.example

import io.ktor.client.plugins.sse.*
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.collectIndexed
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testEvents() {
        testApplication {
            application {
                module()
            }

            val client = createClient {
                install(SSE)
            }

            client.sse("/events") {
                incoming.collectIndexed { i, event ->
                    assertEquals("this is SSE #$i", event.data)
                }
            }
        }
    }

    @Test
    fun testJsonEvents() {
        testApplication {
            application {
                module()
            }

            val client = createClient {
                install(SSE)
            }

            client.sse("/json") {
                incoming.collectIndexed { i, event ->
                    when (i) {
                        0 -> assertEquals("""{"id":0,"firstName":"Jet","lastName":"Brains"}""", event.data)
                        1 -> assertEquals("""{"id":0,"prices":[100,200]}""", event.data)
                    }
                }
            }
        }
    }
}

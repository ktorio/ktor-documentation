package com.example

import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testConversation() {
        testApplication {
            val client = createClient {
                install(WebSockets)
            }

            client.webSocket("/chat") {
                val greetingText = (incoming.receive() as? Frame.Text)?.readText() ?: ""
                assertEquals("You are connected! There are 1 users here.", greetingText)

                send(Frame.Text("Hello, I was first!"))
                val responseText = (incoming.receive() as Frame.Text).readText()
                assertEquals("[user0]: Hello, I was first!", responseText)
            }
        }
    }
}

package com.example

import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testConversation() {
        withTestApplication(Application::module) {
            handleWebSocketConversation("/chat") { incoming, outgoing ->
                val greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("You are connected! There are 1 users here.", greetingText)

                outgoing.send(Frame.Text("Hello, I was first!"))
                val responseText = (incoming.receive() as Frame.Text).readText()
                assertEquals("[user0]: Hello, I was first!", responseText)
            }
        }
    }
}
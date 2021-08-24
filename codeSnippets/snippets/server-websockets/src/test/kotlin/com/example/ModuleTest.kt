package com.example

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import kotlin.test.*

class ModuleTest {
    @Test
    fun testConversation() {
        withTestApplication(Application::module) {
            handleWebSocketConversation("/echo") { incoming, outgoing ->
                val greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("Please enter your name", greetingText)

                outgoing.send(Frame.Text("JetBrains"))
                val responseText = (incoming.receive() as Frame.Text).readText()
                assertEquals("Hi, JetBrains!", responseText)

                outgoing.send(Frame.Text("bye"))
                val closeReason = (incoming.receive() as Frame.Close).readReason()?.message
                assertEquals("Client said BYE", closeReason)
            }
        }
    }
}
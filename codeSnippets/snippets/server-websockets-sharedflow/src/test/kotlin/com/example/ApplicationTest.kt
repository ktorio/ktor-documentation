package com.example

import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlin.test.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ApplicationTest {
    @Test
    fun testConversation() {
        testApplication {
            application {
                module()
            }
            coroutineScope {
                val client1 = createClient {
                    install(WebSockets)
                }
                val client2 = createClient {
                    install(WebSockets)
                }

                launch {
                    client1.webSocket("/ws") {
                        assertEquals("You are connected to WebSocket!", (incoming.receive() as Frame.Text).readText())
                        send(Frame.Text("Hello from client1"))
                        assertEquals("Hello from client1", (incoming.receive() as Frame.Text).readText())
                        assertEquals("Hello from client2", (incoming.receive() as Frame.Text).readText())
                    }
                }

                launch {
                    client2.webSocket("/ws") {
                        assertEquals("You are connected to WebSocket!", (incoming.receive() as Frame.Text).readText())
                        delay(100) // Ensure client1's message is sent first
                        send(Frame.Text("Hello from client2"))
                        assertEquals("Hello from client1", (incoming.receive() as Frame.Text).readText())
                        assertEquals("Hello from client2", (incoming.receive() as Frame.Text).readText())
                    }
                }
            }
        }
    }
}

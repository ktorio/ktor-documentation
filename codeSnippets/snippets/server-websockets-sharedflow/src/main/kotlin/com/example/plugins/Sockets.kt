package com.example.plugins

import com.example.*
import com.example.models.MessageResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val messageResponseFlow = MutableSharedFlow<MessageResponse>()
        val sharedFlow = messageResponseFlow.asSharedFlow()

        webSocket("/ws") {
            send("You are connected to WebSocket!")

            val job = launch {
                sharedFlow.collect { message ->
                    send(message.message)
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        val messageResponse = MessageResponse(receivedText)
                        messageResponseFlow.emit(messageResponse)
                    }
                }
            }.onFailure { exception ->
                println("WebSocket exception: ${exception.localizedMessage}")
            }.also {
                job.cancel()
            }
        }
    }
}

package com.example.plugins

import com.example.*
import data.source.remote.model.MessageResponse
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.time.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
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

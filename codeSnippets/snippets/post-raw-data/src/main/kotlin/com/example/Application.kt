package com.example

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.core.*
import java.io.*

fun Application.main() {
    routing {
        post("/text") {
            val text = call.receiveText()
            call.respondText(text)
        }

        post("/channel") {
            val readChannel = call.receiveChannel()
            val text = readChannel.readRemaining().readText()
            call.respondText(text)
        }

        post("/upload") {
            val channel = call.receiveChannel()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val file = File("uploads/ktor_logo.png")
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                }
            }
            call.respondText("A file is uploaded")
        }
    }
}

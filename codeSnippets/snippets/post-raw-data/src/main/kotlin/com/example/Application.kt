package com.example

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.io.readString
import java.io.*

fun Application.main() {
    routing {
        post("/text") {
            val text = call.receiveText()
            call.respondText(text)
        }

        post("/bytes") {
            val bytes = call.receive<ByteArray>()
            call.respond(String(bytes))
        }

        post("/channel") {
            val readChannel = call.receiveChannel()
            val text = readChannel.readRemaining().readString()
            call.respondText(text)
        }

        post("/upload") {
            val file = File("uploads/ktor_logo.png")
            call.receiveChannel().copyAndClose(file.writeChannel())
            call.respondText("A file is uploaded")
        }
    }
}

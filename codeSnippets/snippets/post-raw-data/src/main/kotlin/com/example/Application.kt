package com.example

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
    }
}

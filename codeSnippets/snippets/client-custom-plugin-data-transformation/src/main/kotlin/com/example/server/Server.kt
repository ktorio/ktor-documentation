package com.example.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Starts a local web server.
 */
fun startServer() = embeddedServer(Netty, port = 8080) {
    routing {
        post("/post-data") {
            val text = call.receiveText()
            call.respondText(text)
        }
        get("/get-data") {
            call.respondText("Jane;33")
        }
    }
}.start()

package com.example

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.main() {
    routing {
        get("/") {
            call.respondText("HTTP version is ${call.request.httpVersion}")
        }
    }
}

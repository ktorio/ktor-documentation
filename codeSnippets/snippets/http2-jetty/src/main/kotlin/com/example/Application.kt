package com.example

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    routing {
        get("/") {
            call.respondText("HTTP version is ${call.request.httpVersion}")
        }
    }
}

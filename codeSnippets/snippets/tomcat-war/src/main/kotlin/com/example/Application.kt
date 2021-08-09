package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

package io.ktor.snippets.autoreload

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}


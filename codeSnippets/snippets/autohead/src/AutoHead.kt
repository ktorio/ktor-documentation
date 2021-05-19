package io.ktor.snippets.autohead

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

fun Application.main() {
    install(AutoHeadResponse)
    routing {
        get("/home") {
            call.respondText("This is a response to a GET, but HEAD also works")
        }
    }
}


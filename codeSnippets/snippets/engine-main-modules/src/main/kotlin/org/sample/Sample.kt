package org.sample

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.module3() {
    routing {
        get("/module3") {
            call.respondText("Hello from 'module3'!")
        }
    }
}

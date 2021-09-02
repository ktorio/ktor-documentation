package org.sample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.module3() {
    routing {
        get("/module3") {
            call.respondText("Hello from 'module3'!")
        }
    }
}

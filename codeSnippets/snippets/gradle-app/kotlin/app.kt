// src/main/kotlin/app.kt

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    val server = embeddedServer(CIO, port = 8080) {
        routing {
            get("/") {
                call.respondText { "Hello" }
            }
        }
    }
    server.start(wait = true)
}
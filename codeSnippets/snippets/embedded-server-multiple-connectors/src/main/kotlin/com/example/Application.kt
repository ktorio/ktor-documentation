package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val appProperties = applicationProperties {
        module { module() }
    }
    embeddedServer(Netty, appProperties) {
        envConfig()
    }.start(true)
}

fun ApplicationEngine.Configuration.envConfig() {
    connector {
        host = "0.0.0.0"
        port = 8080
    }
    connector {
        host = "127.0.0.1"
        port = 9090
    }
}

fun Application.module() {
    routing {
        get("/") {
            if (call.request.local.serverPort == 8080) {
                call.respondText("Connected to public API")
            } else {
                call.respondText("Connected to private API")
            }
        }
    }
}

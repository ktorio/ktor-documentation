package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val env = applicationEngineEnvironment {
        envConfig()
    }
    embeddedServer(Netty, env).start(true)
}

fun ApplicationEngineEnvironmentBuilder.envConfig() {
    module {
        module()
    }
    connector {
        host = "0.0.0.0"
        port = 8080
    }
    connector {
        host = "0.0.0.0"
        port = 8081
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

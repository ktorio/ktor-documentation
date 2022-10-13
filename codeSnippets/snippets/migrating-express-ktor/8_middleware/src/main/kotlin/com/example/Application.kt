package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(RequestLoggingPlugin)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/about") {
            call.respondText("About page")
        }
    }
}

val RequestLoggingPlugin = createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall { call ->
        call.request.origin.apply {
            println("Request URL: $scheme://$host:$port$uri")
        }
    }
}

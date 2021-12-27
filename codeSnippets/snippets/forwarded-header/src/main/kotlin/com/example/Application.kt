package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(XForwardedHeaderSupport)
    routing {
        get("/hello") {
            val localHost = call.request.local.remoteHost
            val localPort = call.request.local.port
            val remoteHost = call.request.origin.remoteHost
            val remotePort = call.request.origin.port
            call.respondText("Proxy request host/port: $localHost, $localPort\n" +
                    "Original request host/port: $remoteHost, $remotePort"
            )
        }
    }
}

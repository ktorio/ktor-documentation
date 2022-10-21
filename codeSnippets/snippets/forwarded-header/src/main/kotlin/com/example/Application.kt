package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ForwardedHeaders)
    install(XForwardedHeaders)
    routing {
        get("/hello") {
            val localHost = call.request.local.serverHost
            val localPort = call.request.local.serverPort
            val remoteHost = call.request.origin.serverHost
            val remotePort = call.request.origin.serverPort
            call.respondText("Proxy request host/port: $localHost, $localPort\n" +
                    "Original request host/port: $remoteHost, $remotePort"
            )
        }
    }
}

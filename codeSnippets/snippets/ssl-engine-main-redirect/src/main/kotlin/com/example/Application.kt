package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedsupport.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(HttpsRedirect) {
        sslPort = 8443
        permanentRedirect = true
    }
    install(XForwardedHeaderSupport)
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

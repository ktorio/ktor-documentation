package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(HSTS) {
        maxAgeInSeconds = 10
        withHost("sample-host") {
            maxAgeInSeconds = 60
            includeSubDomains = false
        }
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

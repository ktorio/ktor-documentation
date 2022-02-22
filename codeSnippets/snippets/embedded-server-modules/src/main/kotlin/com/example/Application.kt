package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        module1()
        module2()
    }.start(wait = true)
}

fun Application.module1() {
    routing {
        get("/module1") {
            call.respondText("Hello from 'module1'!")
        }
    }
}

fun Application.module2() {
    routing {
        get("/module2") {
            call.respondText("Hello from 'module2'!")
        }
    }
}

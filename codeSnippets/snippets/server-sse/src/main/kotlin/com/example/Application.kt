package com.example

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(SSE)

    routing {
        sse("/events") {
            repeat(6) {
                send(ServerSentEvent("this is SSE #$it"))
                delay(1000)
            }
        }
    }
}

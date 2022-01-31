package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.slf4j.event.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(DoubleReceive) {
        cacheRawRequest = true
    }
    install(CallLogging) {
        level = Level.TRACE
        format { call ->
            runBlocking {
                "Body: ${call.receiveText()}"
            }
        }
    }
    routing {
        post("/") {
            val receivedText = call.receiveText()
            call.respondText("Text '$receivedText' is received")
        }
    }
}

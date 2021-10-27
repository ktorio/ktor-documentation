package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(CallId) {
        verify { callId: String ->
            callId.isNotEmpty()
        }
        header(HttpHeaders.XRequestId)
    }
    install(CallLogging) {
        callIdMdc("call-id")
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

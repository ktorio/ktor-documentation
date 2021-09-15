package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.time.*

fun Application.main() {
    intercept(ApplicationCallPipeline.Plugins) {
        delay(2000L)
    }
    routing {
        get("/path1") {
            call.respondText("Response time: ${LocalTime.now()}")
        }
        get("/path2") {
            call.respondText("Response time: ${LocalTime.now()}")
        }
    }
}

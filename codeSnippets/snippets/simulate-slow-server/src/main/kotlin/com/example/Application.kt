package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime

fun Application.main() {
    intercept(ApplicationCallPipeline.Features) {
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

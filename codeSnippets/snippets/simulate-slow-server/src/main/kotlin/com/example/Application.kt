package com.example

import io.ktor.http.*
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
        var requestCount: Int = 0
        get("/error") {
            requestCount += 1
            when (requestCount) {
                in 1..2 -> call.respondText("Server is down", status = HttpStatusCode.InternalServerError)
                in 3..10 -> call.respondText("Server is back online!", status = HttpStatusCode.OK)
            }
        }
    }
}

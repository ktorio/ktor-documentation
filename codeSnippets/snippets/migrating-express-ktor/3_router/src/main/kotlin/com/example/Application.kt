package com.example

import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        // 1. Route methods
        get("/") {
            call.respondText("GET request to the homepage")
        }
        post("/") {
            call.respondText("POST request to the homepage")
        }

        // 2. Group routes by paths
        route("book") {
            get {
                call.respondText("Get a random book")
            }
            post {
                call.respondText("Add a book")
            }
            put {
                call.respondText("Update the book")
            }
        }

        // 3. Group routes by file
        birdsRoutes()
    }
}

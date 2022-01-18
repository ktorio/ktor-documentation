package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.module() {
    routing {
        static("/") {
            staticBasePackage = "static"
            defaultResource("index.html")
            static("assets") {
                resources("css")
                resources("js")
            }
            static("images") {
                resource("ktor_logo.png")
                resource("image.png", "ktor_logo.png")
            }
        }
    }
}

package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*


fun Application.module() {
    routing {
        static("/") {
            staticBasePackage = "static"
            resource("index.html")
            defaultResource("index.html")
            static("images") {
                resource("ktor_logo.png")
                resource("image.png", "ktor_logo.png")
            }
            static("assets") {
                resources("css")
                resources("js")
            }
        }
    }
}

package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        static("/") {
            staticRootFolder = File("public")
            files(".")
            default("index.html")
        }
    }
}

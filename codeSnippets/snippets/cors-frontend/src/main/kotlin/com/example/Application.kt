package com.example

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.io.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    script(src = "/static/script.js") {}
                }
                body {
                    button {
                        onClick = "saveCustomer()"
                        + "Make a CORS request to save a customer"
                    }
                }
            }
        }
        static("static") {
            staticRootFolder = File("files")
            files("js")
        }
    }
}

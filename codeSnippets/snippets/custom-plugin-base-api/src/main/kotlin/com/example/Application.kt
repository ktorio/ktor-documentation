package com.example

import com.example.plugins.CustomHeader
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(CustomHeader) {
        headerName = "X-Custom-Header"
        headerValue = "Hello, world!"
    }
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor: custom-plugin" }
                }
                body {
                    p {
                        +"Hello from Ktor custom plugin sample application"
                    }
                }
            }
        }
    }
}

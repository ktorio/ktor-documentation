package io.ktor.snippets.plugin

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(CustomHeader) { // Install a custom plugin
        headerName = "Hello" // configuration
        headerValue = "World"
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

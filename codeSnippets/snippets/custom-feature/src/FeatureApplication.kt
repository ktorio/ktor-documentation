package io.ktor.snippets.feature

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CustomHeader) { // Install a custom feature
        headerName = "Hello" // configuration
        headerValue = "World"
    }
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor: custom-feature" }
                }
                body {
                    p {
                        +"Hello from Ktor custom feature sample application"
                    }
                }
            }
        }
    }
}

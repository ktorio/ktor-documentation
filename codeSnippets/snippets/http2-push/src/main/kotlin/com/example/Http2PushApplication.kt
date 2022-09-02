package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

@OptIn(UseHttp2Push::class)
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/") {
            call.push("/style.css")
            call.respondHtml {
                head {
                    title { +"Ktor: http2-push" }
                    styleLink("/style.css")
                }
                body {
                    p {
                        +"Hello from Ktor HTTP/2 push sample application"
                    }
                }
            }
        }
        get("/style.css") {
            call.respondText("p { color: blue }", contentType = ContentType.Text.CSS)
        }
    }
}

package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor: Docker" }
                }
                body {
                    val runtime = Runtime.getRuntime()
                    p { +"Hello from Ktor Netty engine running in Docker sample application" }
                    p { +"Runtime.getRuntime().availableProcessors(): ${runtime.availableProcessors()}" }
                    p { +"Runtime.getRuntime().freeMemory(): ${runtime.freeMemory()}" }
                    p { +"Runtime.getRuntime().totalMemory(): ${runtime.totalMemory()}" }
                    p { +"Runtime.getRuntime().maxMemory(): ${runtime.maxMemory()}" }
                    p { +"System.getProperty(\"user.name\"): ${System.getProperty("user.name")}" }
                }
            }
        }
    }
}

package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.module() {
    routing {
        singlePageApplication {
            useResources = true
            filesPath = "sample-web-app"
            defaultPage = "main.html"
            ignoreFiles { it.endsWith(".txt") }
//            react("react-app")
        }
    }
}

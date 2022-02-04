package com.example

import io.ktor.plugins.spa.*
import io.ktor.server.application.*

fun Application.module() {
    install(SinglePageApplication) {
        useResources = true
        filesPath = "sample-web-app"
        defaultPage = "main.html"
        ignoreFiles { it.endsWith(".txt") }
//        react("react-app")
    }
}

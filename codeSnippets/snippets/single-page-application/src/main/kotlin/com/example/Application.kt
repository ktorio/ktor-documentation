package com.example

import io.ktor.plugins.spa.*
import io.ktor.server.application.*

fun Application.module() {
    install(SinglePageApplication) {
        filesPath = "sample-web-app"
        defaultPage = "main.html"
        ignoreFiles { it.endsWith(".txt") }
/*        useResources = true
        react("react-app")*/
    }
}

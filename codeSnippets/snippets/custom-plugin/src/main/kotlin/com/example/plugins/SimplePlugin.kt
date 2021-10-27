package com.example.plugins

import io.ktor.server.application.plugins.api.*

val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
    println("SimplePlugin is installed!")
}

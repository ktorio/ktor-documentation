package com.example.plugins

import io.ktor.server.application.plugins.api.*

val SimplePlugin = ServerPlugin.createApplicationPlugin(name = "SimplePlugin") {
    println("SimplePlugin is installed!")
}

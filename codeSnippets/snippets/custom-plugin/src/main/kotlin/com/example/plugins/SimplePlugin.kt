package com.example.plugins

import io.ktor.server.application.plugins.api.ServerPlugin.Companion.createApplicationPlugin

val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
    println("SimplePlugin is installed!")
}

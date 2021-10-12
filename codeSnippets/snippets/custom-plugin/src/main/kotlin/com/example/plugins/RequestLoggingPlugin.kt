package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.server.plugins.*

val RequestLoggingPlugin = ServerPlugin.createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall { call ->
        call.request.origin.apply {
            println("Request URL: $scheme://$host:$port$uri")
        }
    }
}

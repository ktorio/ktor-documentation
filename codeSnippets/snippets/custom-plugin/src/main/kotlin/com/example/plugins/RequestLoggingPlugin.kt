package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*

val RequestLoggingPlugin = ServerPlugin.createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall { call ->
        println("Origin host: ${call.request.origin.host}, request URI: ${call.request.uri}")
    }
}
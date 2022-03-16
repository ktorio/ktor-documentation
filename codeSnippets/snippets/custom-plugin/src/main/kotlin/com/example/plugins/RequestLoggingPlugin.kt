package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.*

val RequestLoggingPlugin = createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall { call ->
        call.request.origin.apply {
            println("Request URL: $scheme://$host:$port$uri")
        }
    }
    on(MonitoringEvent(ApplicationStopped)) {
        println("Server is stopped")
    }
}

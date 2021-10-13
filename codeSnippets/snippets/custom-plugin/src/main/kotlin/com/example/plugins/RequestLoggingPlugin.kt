package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.server.plugins.*

val RequestLoggingPlugin = ServerPlugin.createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall { call ->
        call.request.origin.apply {
            println("Request URL: $scheme://$host:$port$uri")
        }
        call.afterFinish { throwable ->
            if(throwable?.cause != null) {
                println("Exception ${throwable.cause} happened during a call to \"${call.request.origin.uri}\"")
            }
        }
    }
    applicationShutdownHook {
        println("Server is stopped")
    }
}

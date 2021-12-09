package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*

val RequestLoggingPlugin = createApplicationPlugin(name = "RequestLoggingPlugin") {
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
    on(Shutdown) {
        println("Server is stopped")
    }
}

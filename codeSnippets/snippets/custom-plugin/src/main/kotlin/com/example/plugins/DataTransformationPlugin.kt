package com.example.plugins

import io.ktor.server.application.plugins.api.*

val DataTransformationPlugin = ServerPlugin.createApplicationPlugin(name = "DataTransformationPlugin") {
    onCallReceive { call ->
        transformRequestBody { data ->
            "Hello World"
        }
    }

    onCallRespond { call ->
        transformResponseBody { data ->
            val result = when (data) {
                is String -> data.reversed()
                else -> data
            }
            return@transformResponseBody result
        }
    }
}
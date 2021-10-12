package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.server.request.*
import io.ktor.utils.io.*

val DataTransformationPlugin = ServerPlugin.createApplicationPlugin(name = "DataTransformationPlugin") {
    onCallReceive { call ->
        transformRequestBody { data ->

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
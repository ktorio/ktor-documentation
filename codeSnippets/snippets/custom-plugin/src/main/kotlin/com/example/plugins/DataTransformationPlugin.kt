package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.utils.io.*

val DataTransformationPlugin = ServerPlugin.createApplicationPlugin(name = "DataTransformationPlugin") {
/*    onCallReceive { call ->
        transformRequestBody { data ->
            when (data) {
                is Int -> data + 1
                else -> data
            }
        }
    }*/

    onCallRespond { call ->
        transformResponseBody { data ->
            // When responding numbers, increment:
            val result = when (data) {
                is String -> data.reversed()
                else -> data
            }
            return@transformResponseBody result
        }
    }
}
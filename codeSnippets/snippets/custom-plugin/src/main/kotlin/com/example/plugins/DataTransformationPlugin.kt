package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.utils.io.*

val DataTransformationPlugin = createApplicationPlugin(name = "DataTransformationPlugin") {
    onCallReceive { call ->
        transformBody { data ->
            if (requestedType?.type == Int::class) {
                val line = data.readUTF8Line() ?: "1"
                line.toInt() + 1
            } else {
                data
            }
        }
    }

    onCallRespond { call ->
        transformBody { data ->
            if (data is Int) {
                (data + 1).toString()
            } else {
                data
            }
        }
    }
}

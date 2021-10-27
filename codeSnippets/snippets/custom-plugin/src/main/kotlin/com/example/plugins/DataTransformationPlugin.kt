package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.utils.io.*

val DataTransformationPlugin = createApplicationPlugin(name = "DataTransformationPlugin") {
    onCallReceive { call ->
        transformBody { data ->
            if (requestedType?.type == Int::class) {
                data.readInt() + 1
            } else {
                data
            }
        }
    }

    onCallRespond { call ->
        transformBody { data ->
            if (data is Int) {
                val channel = ByteChannel(false)
                channel.writeInt(data + 1)
                channel
            } else {
                data
            }
        }
    }
}
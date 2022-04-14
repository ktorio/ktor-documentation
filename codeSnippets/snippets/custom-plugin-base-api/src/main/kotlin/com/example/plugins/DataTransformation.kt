package com.example.plugins

import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.utils.io.*

class DataTransformation {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, DataTransformation> {
        override val key = AttributeKey<DataTransformation>("DataTransformation")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): DataTransformation {
            val plugin = DataTransformation()
            pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Transform) { value ->
                val newValue = (value as ByteReadChannel).readUTF8Line()?.toInt()?.plus(1)
                proceedWith(newValue!!)
            }
            pipeline.sendPipeline.intercept(ApplicationSendPipeline.Transform) { value ->
                val newValue = value.toString().toInt() + 1
                proceedWith(newValue.toString())
            }
            return plugin
        }
    }
}
package com.example.plugins

import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class RequestLogging() {
    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        context.call.request.origin.apply {
            println("Request URL: $scheme://$host:$port$uri")
        }
    }

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, RequestLogging> {
        override val key = AttributeKey<RequestLogging>("RequestLogging")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RequestLogging {
            val plugin = RequestLogging()
            pipeline.intercept(ApplicationCallPipeline.Monitoring) {
                plugin.intercept(this)
            }
            return plugin
        }
    }
}

package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class CustomHeader(configuration: Configuration) {
    private val name = configuration.headerName
    private val value = configuration.headerValue

    class Configuration {
        var headerName = "Custom-Header-Name"
        var headerValue = "Default value"
    }

    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        context.call.response.header(name, value)
    }

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
        override val key = AttributeKey<CustomHeader>("CustomHeader")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomHeader {
            val configuration = Configuration().apply(configure)
            val plugin = CustomHeader(configuration)
            pipeline.intercept(ApplicationCallPipeline.Call) {
                plugin.intercept(this)
            }
            return plugin
        }
    }
}

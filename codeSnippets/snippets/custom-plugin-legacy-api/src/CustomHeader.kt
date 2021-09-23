package io.ktor.snippets.plugin

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import io.ktor.server.response.*
import io.ktor.util.*

/**
 * Adds custom header.
 */
class CustomHeader(configuration: Configuration) {
    // get an immutable snapshot of a configuration values
    private val name = configuration.headerName
    private val value = configuration.headerValue

    // Plugin configuration class
    class Configuration {
        // mutable properties with default values so user can modify it
        var headerName = "Custom"
        var headerValue = "Value"
    }

    // Body of the plugin
    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        // Add custom header to the response
        context.call.response.header(name, value)
    }

    /**
     * Installable plugin for [CustomHeader].
     */
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, CustomHeader.Configuration, CustomHeader> {
        override val key = AttributeKey<CustomHeader>("CustomHeader")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomHeader {
            // Call user code to configure a plugin
            val configuration = Configuration().apply(configure)

            // Create a plugin instance
            val plugin = CustomHeader(configuration)

            // Install an interceptor that will be run on each call and call plugin instance
            pipeline.intercept(ApplicationCallPipeline.Call) {
                plugin.intercept(this)
            }

            // Return a plugin instance so that client code can use it
            return plugin
        }
    }
}

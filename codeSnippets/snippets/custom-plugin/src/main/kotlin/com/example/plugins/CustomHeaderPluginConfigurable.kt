package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*

val CustomHeaderPluginConfigurable = createApplicationPlugin(
    name = "CustomHeaderPluginConfigurable",
    configurationPath = "http.custom_header",
    createConfiguration = ::CustomHeaderConfiguration
) {
    val headerName = pluginConfig.headerName
    val headerValue = pluginConfig.headerValue
    pluginConfig.apply {
        onCall { call ->
            call.response.headers.append(headerName, headerValue)
        }
    }
}

class CustomHeaderConfiguration(config: ApplicationConfig) {
    var headerName: String = config.tryGetString("header_name") ?: "Custom-Header-Name"
    var headerValue: String = config.tryGetString("header_value") ?: "Default value"
}

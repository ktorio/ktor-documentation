package com.example.plugins

import io.ktor.server.application.*

val CustomHeaderPlugin = createApplicationPlugin(
    name = "CustomHeaderPlugin",
    createConfiguration = ::PluginConfiguration
) {
    val headerName = pluginConfig.headerName
    val headerValue = pluginConfig.headerValue
    pluginConfig.apply {
        onCall { call ->
            call.response.headers.append(headerName, headerValue)
        }
    }
}

class PluginConfiguration {
    var headerName: String = "Custom-Header-Name"
    var headerValue: String = "Default value"
}

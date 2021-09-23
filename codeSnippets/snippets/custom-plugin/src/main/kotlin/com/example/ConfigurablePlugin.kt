package com.example

import io.ktor.server.application.plugins.api.*

val ConfigurablePlugin = ServerPlugin.createApplicationPlugin(name = "ConfigurablePlugin", createConfiguration = {
    PluginConfiguration()
}) {
    pluginConfig.apply {
        if (showGreeting) {
            println(greeting)
        }
    }
}

public class PluginConfiguration {
    var showGreeting: Boolean = false
    var greeting: String = "Default greeting"
}

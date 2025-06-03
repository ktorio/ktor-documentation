package com.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.tomcat.jakarta.Tomcat

fun runConfiguredTomcatProperties() {
    embeddedServer(Tomcat, configure = {
        configureTomcat = {
            with(connector) {
                enableLookups = false
                setProperty("maxThreads", "150")
            }
        }
    }) {
        module()
    }.start(true)
}
package com.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.jakarta.Jetty
import org.eclipse.jetty.server.handler.ErrorHandler
import kotlin.time.Duration.Companion.seconds

fun runConfiguredJettyProperties() {
    embeddedServer(Jetty, configure = {
        configureServer = {
            errorHandler = MyErrorHandler()
        }
        idleTimeout = 30.seconds
    }) {
        module()
    }.start(true)
}

class MyErrorHandler : ErrorHandler()
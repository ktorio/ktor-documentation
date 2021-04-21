package e2e

import io.ktor.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.helpers.NOPLogger

fun defaultServer(module: Application.() -> Unit) = embeddedServer(CIO, environment = applicationEngineEnvironment {
    log = NOPLogger.NOP_LOGGER

    connector {
        port = 8080
    }

    module(module)
})
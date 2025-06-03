package com.example

import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun runConfiguredCIOProperties() {
    embeddedServer(CIO, configure = {
        connectionIdleTimeoutSeconds = 45
        reuseAddress = false
    }) {
        module()
    }.start(true)
}

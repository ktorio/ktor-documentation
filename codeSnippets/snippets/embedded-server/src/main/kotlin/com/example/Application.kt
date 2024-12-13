package com.example

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/*
   Important: The contents of this file are referenced by line number in `server-configuration-code.topic`,
   `server-engines.md` and `migrating-3.md`. If you add, remove, or modify any lines, ensure you update the corresponding
    line numbers in the `code-block` element of the referenced file.
*/
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Running basic server...")
        println("Provide the 'configured' argument to run a configured server.")
        runBasicServer()
    }

    when (args[0]) {
        "basic" -> runBasicServer()
        "configured" -> runConfiguredServer()
        else -> runServerWithCommandLineConfig(args)
    }
}

fun runBasicServer() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}

fun runConfiguredServer() {
    embeddedServer(Netty, configure = {
        connectors.add(EngineConnectorBuilder().apply {
            host = "127.0.0.1"
            port = 8080
        })
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
        shutdownGracePeriod = 2000
        shutdownTimeout = 3000
    }) {
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}

fun runServerWithCommandLineConfig(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        }
    ) {
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
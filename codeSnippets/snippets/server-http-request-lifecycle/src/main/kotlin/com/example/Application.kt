package com.example

/*
    Important: The contents of this file are referenced by line numbers in `server-http-request-lifecycle.md`.
    If you add, remove, or modify any lines, ensure you update the corresponding
    line numbers in the code-block element of the referenced file.
*/

import io.ktor.server.application.*
import io.ktor.server.http.HttpRequestLifecycle
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(HttpRequestLifecycle) {
        cancelCallOnClose = true
    }

    routing {
        get("/long-process") {
            try {
                while (isActive) {
                    delay(10_000)
                    log.info("Very important work.")
                }
                call.respond("Completed")
            } catch (e: CancellationException) {
                log.info("Cleaning up resources.")
            }
        }
    }
}

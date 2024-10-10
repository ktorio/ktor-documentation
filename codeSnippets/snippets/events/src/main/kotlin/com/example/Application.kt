package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ApplicationMonitoringPlugin)
    monitor.subscribe(ApplicationStarted) { application ->
        application.environment.log.info("Server is started")
    }
    monitor.subscribe(ApplicationStopped) { application ->
        application.environment.log.info("Server is stopped")
        // Release resources and unsubscribe from events
        monitor.unsubscribe(ApplicationStarted) {}
        monitor.unsubscribe(ApplicationStopped) {}
    }
    monitor.subscribe(NotFoundEvent) { call ->
        log.info("No page was found for the URI: ${call.request.uri}")
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*

suspend fun main() {
    val client = HttpClient(CIO) {
        install(WebSockets)

        defaultRequest {
            url("http://$SERVER_HOST:$SERVER_PORT")
        }

        configureMonitoring()
    }

    doRequests(client)
}
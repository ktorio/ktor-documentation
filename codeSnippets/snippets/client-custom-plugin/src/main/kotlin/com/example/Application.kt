package com.example

import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(CustomHeaderConfigurablePlugin) {
                headerName = "X-Custom-Header"
                headerValue = "Hello, world!"
            }
            install(LoggingHeadersPlugin)
            install(ResponseTimePlugin)
        }

        client.get("https://ktor.io/")
    }
}

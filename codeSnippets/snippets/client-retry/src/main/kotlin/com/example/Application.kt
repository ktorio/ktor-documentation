package com.example

import slowserver.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.coroutines.*

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
            install(Logging) { level = LogLevel.INFO }
        }

        val response: HttpResponse = client.get("http://0.0.0.0:8080/error")
        println(response.bodyAsText())
    }
}

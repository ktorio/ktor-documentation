package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main() {
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
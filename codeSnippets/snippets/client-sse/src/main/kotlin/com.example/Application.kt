package com.example

import io.ktor.client.*
import io.ktor.client.plugins.sse.*
import kotlinx.coroutines.*


fun main() {

    val client = HttpClient {
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
    }
    runBlocking {
        client.sse(host = "0.0.0.0", port = 8080, path = "/events") {
            while (true) {
                incoming.collect { event ->
                    println("Event from server:")
                    println(event)
                }
            }
        }
    }
    client.close()
}
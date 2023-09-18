package com.example

import io.ktor.client.*
import io.ktor.client.plugins.sse.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds


fun main() {

    val client = HttpClient() {
        install(SSE) {
            reconnectionTime = 3000.milliseconds
        }
    }
    runBlocking {
        client.sse(host = "0.0.0.0", port = 8080, path = "/events") {
            while(true) {
                incoming.collect { response ->
                    println("Event from server:")
                    println(response)
                }
            }
        }
    }
    client.close()
}
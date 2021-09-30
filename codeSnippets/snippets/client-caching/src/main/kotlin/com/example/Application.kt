package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpCache)
            install(Logging) { level = LogLevel.INFO }
        }

        client.get("http://localhost:8080/customer/1")
        client.get("http://localhost:8080/customer/1")
    }
}

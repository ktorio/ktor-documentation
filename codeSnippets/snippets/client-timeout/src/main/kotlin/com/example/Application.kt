package com.example

import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import slowserver.*
import java.time.LocalTime

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 1000
            }
        }

        val requestTime = LocalTime.now()
        val response: HttpResponse = client.get("http://0.0.0.0:8080/path1") {
            timeout {
                requestTimeoutMillis = 3000
            }
        }

        println("Request time: $requestTime")
        println(response.bodyAsText())
    }
}
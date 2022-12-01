package com.example

import com.example.plugins.*
import com.example.server.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main() {
    startServer()
    runBlocking {
        val client = HttpClient(CIO) {
            install(AuthPlugin) {
                token = "abc123"
            }
        }
        val response = client.get("http://0.0.0.0:8080/")
        println(response.bodyAsText())
    }
}

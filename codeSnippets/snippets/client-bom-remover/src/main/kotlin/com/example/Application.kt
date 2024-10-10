package com.example

import compression.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.bomremover.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.coroutines.*

fun main() {
    defaultServer(Application::module).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(BOMRemover)
        }
        
        val response  = client.get("http://0.0.0.0:8080/")
        println("Body: ${response.bodyAsText()}")
        client.close()
    }
}

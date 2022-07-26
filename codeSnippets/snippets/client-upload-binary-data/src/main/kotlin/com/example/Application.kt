package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import kotlinx.coroutines.*
import java.io.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO)
        val response = client.post("http://0.0.0.0:8080/upload") {
            setBody(File("ktor_logo.png").readChannel())
        }
        println(response.bodyAsText())
    }
}

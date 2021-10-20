package com.example

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(Apache)
        client[HttpSend].intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                originalCall
            }
        }

        val response: HttpResponse = client.get("https://ktor.io")
        println(response.status)
    }
}

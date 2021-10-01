package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO)
        client[HttpSend].intercept { request ->
            val originalCall = execute(request)
            println(message = "Request time: ${originalCall.response.requestTime.timestamp}")
            println(message = "Response time: ${originalCall.response.responseTime.timestamp}")
            return@intercept originalCall
        }

        val response: HttpResponse = client.get("https://ktor.io/")
    }
}
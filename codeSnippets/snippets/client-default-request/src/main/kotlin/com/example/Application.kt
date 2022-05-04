package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ktor.io"
                    path("docs/")
                    parameters.append("token", "abc123")
                }
                header("X-Custom-Header", "Hello")
            }
        }

        val response: HttpResponse = client.get("welcome.html")
        println(response.status)
    }
}

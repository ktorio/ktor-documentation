package com.example

import compression.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.*

fun main() {
    defaultServer(Application::module).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
        }

        val response: HttpResponse = client.get("http://0.0.0.0:8080/")
        println("Content-Encoding: ${response.headers[HttpHeaders.ContentEncoding]}")
        println("Body: ${response.bodyAsText()}")
        client.close()
    }
}

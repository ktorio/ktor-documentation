package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

fun createClient(isDevelopment: Boolean): HttpClient = HttpClient(CIO) {
    install(Logging) {
        logger = Logger.DEFAULT
        level = if (isDevelopment) LogLevel.HEADERS else LogLevel.NONE
        filter { request ->
            request.url.host.contains("ktor.io")
        }
        sanitizeHeader { header -> header.equals(HttpHeaders.Authorization, ignoreCase = true) }
    }
}

fun main(args: Array<String>) {
    val isDevelopment = "--development" in args
    runBlocking {
        createClient(isDevelopment).use { client ->
            val response1: HttpResponse = client.get("https://ktor.io/")
            val response2: HttpResponse = client.get("https://jetbrains.com/")
        }
    }
}

package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpCookies) {
                storage
            }
        }
        val loginResponse: HttpResponse = client.get("http://0.0.0.0:8080/login")
        repeat(3) {
            val response: HttpResponse = client.get("http://0.0.0.0:8080/")
            println(response.bodyAsText())
        }
    }
}
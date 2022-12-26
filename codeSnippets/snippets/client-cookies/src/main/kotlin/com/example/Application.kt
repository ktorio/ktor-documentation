package com.example

import cookieclient.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpCookies)
        }
        val loginResponse: HttpResponse = client.get("http://0.0.0.0:8080/login")
        repeat(3) {
            val response: HttpResponse = client.get("http://0.0.0.0:8080/user")
            println(response.bodyAsText())
        }
        client.close()
    }
}
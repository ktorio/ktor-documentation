package com.example

import formparameters.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.*

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.submitForm(
            url = "http://localhost:8080/signup",
            formParameters = parameters {
                append("username", "JetBrains")
                append("email", "example@jetbrains.com")
                append("password", "foobar")
                append("confirmation", "foobar")
            }
        )
        println(response.bodyAsText())
    }
}

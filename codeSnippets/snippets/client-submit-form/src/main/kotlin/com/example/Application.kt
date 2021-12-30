package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO)

        val response: HttpResponse = client.submitForm(
            url = "http://localhost:8080/signup",
            formParameters = Parameters.build {
                append("username", "JetBrains")
                append("email", "example@jetbrains.com")
                append("password", "foobar")
                append("confirmation", "foobar")
            }
        )

        println(response.bodyAsText())
    }
}

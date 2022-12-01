package com.example

import com.example.model.*
import com.example.plugins.*
import com.example.server.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main() {
    startServer()
    runBlocking {
        val client = HttpClient(CIO) {
            install(DataTransformationPlugin)
        }
        val bodyAsText = client.post("http://0.0.0.0:8080/post-data") {
            setBody(User("John", 42))
        }.bodyAsText()
        val user = client.get("http://0.0.0.0:8080/get-data").body<User>()
        println("Userinfo: $bodyAsText")
        println("Username: ${user.name}, age: ${user.age}")
    }
}

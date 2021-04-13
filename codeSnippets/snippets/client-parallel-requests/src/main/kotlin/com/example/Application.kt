package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val client = HttpClient(CIO)

        // Sequential requests
/*        val firstRequestContent = client.get<String>("http://localhost:8080/path1")
        val secondRequestContent = client.get<String>("http://localhost:8080/path2")*/

        // Parallel requests
        val firstRequest: Deferred<String> = async { client.get("http://localhost:8080/path1") }
        val secondRequest: Deferred<String> = async { client.get("http://localhost:8080/path2") }
        val firstRequestContent = firstRequest.await()
        val secondRequestContent = secondRequest.await()

        println(firstRequestContent+"\n"+secondRequestContent)

        client.close()
    }
}


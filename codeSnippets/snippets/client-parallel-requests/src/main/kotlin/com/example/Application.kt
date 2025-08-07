package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    coroutineScope {
        val client = HttpClient(CIO)

        // Sequential requests
/*        val firstRequestContent: String = client.get("http://localhost:8080/path1").bodyAsText()
        val secondRequestContent: String = client.get("http://localhost:8080/path2").bodyAsText()*/

        // Parallel requests
        val firstRequest: Deferred<String> = async { client.get("http://localhost:8080/path1").bodyAsText() }
        val secondRequest: Deferred<String> = async { client.get("http://localhost:8080/path2").bodyAsText() }
        val firstRequestContent = firstRequest.await()
        val secondRequestContent = secondRequest.await()

        println(firstRequestContent+"\n"+secondRequestContent)

        client.close()
    }
}


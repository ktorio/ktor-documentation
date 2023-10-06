package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.callid.*
import io.ktor.http.*
import kotlinx.coroutines.*

suspend fun main() {
    getRequestWithCallIdFromContext()
    getRequestWithCustomCallId()
}

suspend fun getRequestWithCallIdFromContext() {
    val client = HttpClient(CIO) {
        install(CallId) {
            generate { "call-id-client" }
            addToHeader(HttpHeaders.XRequestId)
        }
    }
    client.use {
        val response: HttpResponse = client.get("http://0.0.0.0:8090/call")
        println(response.bodyAsText())

        val responseFromDoubleCall: HttpResponse = client.get("http://0.0.0.0:8090/double-call")
        println(responseFromDoubleCall.bodyAsText())
    }
}

suspend fun getRequestWithCustomCallId() {
    val client = HttpClient(CIO) {
        install(CallId) {
            useCoroutineContext = false
            generate { "call-id-client-2" }
        }
    }
    client.use {
        val response: HttpResponse = client.get("http://0.0.0.0:8090/call")
        println(response.bodyAsText())

    }
}

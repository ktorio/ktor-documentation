package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.callid.*
import io.ktor.http.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(CallId) {
                generate { "call-id-client" }
                addToHeader(HttpHeaders.XRequestId)
                intercept { request, callId -> request.parameter("callId", callId) }
            }
        }

        val response: HttpResponse = client.get("http://0.0.0.0:8090/call")
        println(response.bodyAsText())

        val responseFromDoubleCall: HttpResponse = client.get("http://0.0.0.0:8090/double-call")
        println(responseFromDoubleCall.bodyAsText())

        val client2 = HttpClient(CIO) {
            install(CallId) {
                useCoroutineContext = false
                generate { "call-id-client-2" }
                intercept { request, callId -> request.parameter("callId", callId) }
            }
        }

        val response2: HttpResponse = client2.get("http://0.0.0.0:8090/call")
        println(response2.bodyAsText())
        client.close()
    }
}

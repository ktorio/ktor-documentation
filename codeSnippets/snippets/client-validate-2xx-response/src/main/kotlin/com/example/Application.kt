package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class Error(val code: Int, val message: String)
class CustomResponseException(response: HttpResponse, cachedResponseText: String) :
    ResponseException(response, cachedResponseText) {
    override val message: String = "Custom server error: ${response.call.request.url}. " +
            "Status: ${response.status}. Text: \"$cachedResponseText\""
}

fun main() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        HttpResponseValidator {
            validateResponse { response ->
                val error: Error = response.body()
                if (error.code != 0) {
                    throw CustomResponseException(response, "Code: ${error.code}, message: ${error.message}")
                }
            }
        }
    }

    runBlocking {
        val httpResponse: HttpResponse = try {
            client.get("http://localhost:8080/error")
        } catch (cause: ResponseException) {
            println(cause)
            cause.response
        }
    }
}

package com.example

import io.ktor.client.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.sse.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

@Serializable
data class Product(val id: Int, val prices: List<Int>)

fun main() {
    val client = HttpClient {
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
    }
    runBlocking {
        client.sse(host = "0.0.0.0", port = 8080, path = "/events") {
            while (true) {
                incoming.collect { event ->
                    println("Event from server:")
                    println(event)
                }
            }
        }

        // example with deserialization
        client.sse({
            url("http://localhost:8080/serverSentEvents")
        }, deserialize = {
                typeInfo, jsonString ->
            val serializer = Json.serializersModule.serializer(typeInfo.kotlinType!!)
            Json.decodeFromString(serializer, jsonString)!!
        }) { // `this` is `ClientSSESessionWithDeserialization`
            incoming.collect { event: TypedServerSentEvent<String> ->
                when (event.event) {
                    "customer" -> {
                        val customer: Customer? = deserialize<Customer>(event.data)
                    }
                    "product" -> {
                        val product: Product? = deserialize<Product>(event.data)
                    }
                }
            }
        }
        client.close()
    }
}
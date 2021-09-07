package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val response: HttpResponse = client.post("http://localhost:8080/customer") {
            contentType(ContentType.Application.Json)
            body = Customer(3,"Jet", "Brains")
        }
        println(response.readText())

        val customer: Customer = client.get("http://localhost:8080/customer/3")
        println("First name: '${customer.firstName}', last name: '${customer.lastName}'")
    }
}

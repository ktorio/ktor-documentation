package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.shared.serialization.kotlinx.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.*

@Serializable
data class IpResponse(val ip: String)

fun main() {
    runBlocking {
        val client = ApiClient(CIO.create())
        val response = client.getIp()
        println(response.ip)
    }
}

class ApiClient(engine: HttpClientEngine) {
    private val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getIp(): IpResponse = httpClient.get("https://api.ipify.org/?format=json").body()
}

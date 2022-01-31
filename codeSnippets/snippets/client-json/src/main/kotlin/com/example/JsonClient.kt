package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.*

data class Model(val name: String, val items: List<Item>)
data class Item(val key: String, val value: String)

fun main(args: Array<String>) = runBlocking<Unit> {
    val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            gson()
        }
    }
    println("Requesting model...")
    val model: Model = client.get("http://0.0.0.0:8080/v1").body()
    println("Fetching items for '${model.name}'...")
    for ((key, _) in model.items) {
        val item: Item = client.get("http://0.0.0.0:8080/v1/item/$key").body()
        println("Received: $item")
    }
}

package com.example

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        webSocket("/customer/1") {
            sendSerialized(Customer(1, "Jane", "Smith"))
        }
        webSocket("/customer") {
            val customer = receiveDeserialized<Customer>()
            println("A customer with id ${customer.id} is received by the server.")
        }
    }
}

package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun Application.main() {
    install(CORS) {
        host("0.0.0.0:5000")
        header("Content-Type")
    }

    val customerStorage = mutableListOf<Customer>()
    install(ContentNegotiation) {
        json(Json)
    }
    routing {
        post("/customer") {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
    }
}

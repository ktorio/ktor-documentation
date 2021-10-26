package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun Application.main() {
    val customerStorage = mutableListOf<Customer>()
    customerStorage.addAll(
        arrayOf(
            Customer(1, "Jane", "Smith"),
            Customer(2, "John", "Smith")
        )
    )

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        get("/customer/{id}") {
            val id = call.parameters["id"]
            val customer: Customer = customerStorage.find { it.id == id!!.toInt() }!!
            call.respond(customer)
        }

        post("/customer") {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
    }
}

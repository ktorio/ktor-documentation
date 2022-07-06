package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello"))
                ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
        validate<Customer> { customer ->
            if (customer.id >= 10)
                ValidationResult.Invalid("A customer ID should be less than 10")
            else ValidationResult.Valid
        }
        validate {
            filter { body ->
                body is ByteArray
            }
            validation { body ->
                check(body is ByteArray)
                val intValue = String(body).toInt()
                if (intValue < 0)
                    ValidationResult.Invalid("A value should be greater than 0")
                else ValidationResult.Valid
            }
        }
    }
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        post("/text") {
            val body = call.receive<String>()
            call.respond(body)
        }
        post("/json") {
            val customer = call.receive<Customer>()
            call.respond(customer)
        }
        post("/array") {
            val body = call.receive<ByteArray>()
            call.respond(String(body))
        }
    }
}

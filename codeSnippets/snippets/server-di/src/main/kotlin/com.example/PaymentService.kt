package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.annotations.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*

interface PaymentProcessor {
    suspend fun handlePayment(call: ApplicationCall, userId: String, cartId: String, amount: Long)
}
class CreditCardPaymentProvider(
    val baseUrl: String,
    val clientKey: String,
    val hashEncoding: (String) -> String
) : PaymentProcessor {
    override suspend fun handlePayment(call: ApplicationCall, userId: String, cartId: String, amount: Long) {
        call.response.header("X-Transaction-Id", "$userId:$cartId")
        call.response.header("X-Digest", hashEncoding("$clientKey:$userId:$cartId:$amount"))
        call.respondRedirect("$baseUrl/payment/$amount")
    }
}

class PointsBalancePaymentProvider(
    val updatePoints: suspend (String, Long) -> Long
) : PaymentProcessor {
    override suspend fun handlePayment(call: ApplicationCall, userId: String, cartId: String, amount: Long) {
        updatePoints(userId, amount)
        call.respondRedirect("/paymentComplete?userId=$userId&cartId=$cartId&amount=$amount")
    }
}

fun Application.configureExternalPaymentProvider(
    @Property("payments.url") baseUrl: String,
    @Property("payments.clientKey") clientKey: String,
) {
    dependencies {
        provide("external") { CreditCardPaymentProvider(baseUrl, clientKey) { it.reversed() } }
    }
}


fun Application.paymentsHandling(
    @Named("external") payments: PaymentProcessor
) {
    log.info("Using payment processor: $payments")
    routing {
        post("/checkout") {
            val userId = call.request.cookies["userId"]
                ?: return@post call.respondText("Login required", status = HttpStatusCode.Forbidden)
            val cartId = call.request.cookies["cartId"]
                ?: return@post call.respondText("Cart ID missing", status = HttpStatusCode.Forbidden)
            val amount = call.request.queryParameters["amount"]?.toLongOrNull() ?: return@post call.respondText("Amount missing", status = HttpStatusCode.BadRequest)

            payments.handlePayment(call, userId, cartId, amount)
        }
        get("/paymment/{amount}") {
            call.respondText("Payment for ${call.parameters["amount"]} is pending...")
        }
    }
}
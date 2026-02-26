package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondRedirect
import io.ktor.server.testing.*
import kotlin.test.*

class PaymentTest {
    @Test
    fun testCheckoutRedirectViaExternalProcessor() = testApplication {
        application {
            val mockProcessor = MockPaymentProcessor()
            dependencies {
                provide<PaymentProcessor>("external") { mockProcessor }
            }
            paymentsHandling(mockProcessor)
        }

        val response = client.post("/checkout") {
            cookie("userId", "user-42")
            cookie("cartId", "cart-7")
            parameter("amount", "1999")
        }

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/fake-payment-complete", response.headers[HttpHeaders.Location])
    }
}

class MockPaymentProcessor : PaymentProcessor {
    data class RecordedCall(val userId: String, val cartId: String, val amount: Long)

    var lastCall: RecordedCall? = null
        private set

    override suspend fun handlePayment(
        call: ApplicationCall,
        userId: String,
        cartId: String,
        amount: Long,
    ) {
        lastCall = RecordedCall(userId, cartId, amount)
        call.respondRedirect("/fake-payment-complete")
    }
}
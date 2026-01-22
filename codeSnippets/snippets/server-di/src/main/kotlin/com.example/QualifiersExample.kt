package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.di.annotations.Named

interface PaymentProcessor
class MongoPaymentProcessor : PaymentProcessor
class SqlPaymentProcessor : PaymentProcessor

fun Application.paymentModule(
    @Named("mongo") mongo: PaymentProcessor
) {
    log.info("Using payment processor: $mongo")
}

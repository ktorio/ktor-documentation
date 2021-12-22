package com.example

import com.codahale.metrics.*
import com.codahale.metrics.jmx.*
import io.ktor.server.application.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.concurrent.*

fun Application.module() {
    install(DropwizardMetrics) {
        Slf4jReporter.forRegistry(registry)
            .outputTo(log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start(10, TimeUnit.SECONDS)
        JmxReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start()
        registerJvmMetricSets = false
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

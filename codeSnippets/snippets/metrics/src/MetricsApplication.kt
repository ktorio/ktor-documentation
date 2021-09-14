package io.ktor.snippets.metrics

import com.codahale.metrics.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.html.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.concurrent.*

fun Application.main() {
    install(DefaultHeaders)
    install(DropwizardMetrics) {
        val reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
    }
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Ktor: metrics" }
                }
                body {
                    p {
                        +"Hello from Ktor metrics sample application"
                    }
                }
            }
        }
    }
}
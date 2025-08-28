package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.http.*
import io.opentelemetry.instrumentation.ktor.v2_0.common.internal.Experimental
import io.opentelemetry.instrumentation.ktor.v3_0.KtorClientTelemetry

/**
 * Install OpenTelemetry on the client.
 */
fun HttpClientConfig<CIOEngineConfig>.configureMonitoring() {
    val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-sample-client")
    
    install(KtorClientTelemetry) {
        setOpenTelemetry(openTelemetry)

        Experimental.emitExperimentalTelemetry(this)

        knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)
        capturedRequestHeaders(HttpHeaders.Accept)
        capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)

        attributesExtractor {
            onStart {
                attributes.put("start-time", System.currentTimeMillis())
            }
            onEnd {
                attributes.put("end-time", System.currentTimeMillis())
            }
        }
    }
}

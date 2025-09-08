package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry

const val serviceName = "opentelemetry-ktor-sample-server"

fun Application.setupServerTelemetry(): OpenTelemetry {
    val openTelemetry = getOpenTelemetry(serviceName)

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)

        knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)

        capturedRequestHeaders(HttpHeaders.UserAgent)
        capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)

        spanStatusExtractor {
            val path = response?.call?.request?.path() ?: ""
            if (path.contains("/span-status-extractor") || error != null) {
                spanStatusBuilder.setStatus(StatusCode.ERROR)
            }
        }

        spanKindExtractor {
            if (httpMethod == HttpMethod.Post) {
                SpanKind.PRODUCER
            } else {
                SpanKind.CLIENT
            }
        }

        attributesExtractor {
            onStart {
                attributes.put("start-time", System.currentTimeMillis())
            }
            onEnd {
                attributes.put("end-time", System.currentTimeMillis())
            }
        }
    }
    return openTelemetry
}
package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*

/**
 * This example demonstrates usage of HttpTimeout plugin. It consists of two endpoints. First endpoint "/timeout"
 * emulates some long-running process that might hangup. Second endpoint "/proxy" represents a proxy to "/timeout" that
 * protects a user against such hang-ups. If user connects to "/proxy" and request hanged proxy will automatically abort
 * request using HttpTimeout plugin.
 */
fun Application.timeoutApplication() {
    /* Client to perform proxy requests. */
    val client = HttpClient {
        install(HttpTimeout)
    }

    /* Target endpoint. */
    routing {
        get("/timeout") {
            delay(call.parameters["delay"]!!.toLong())
            call.respondText("It's OK!")
        }
    }

    /* Proxy endpoint. */
    routing {
        get("/proxy") {
            try {
                val response = client.get("http://localhost:8080") {
                    parameter("delay", call.parameters["delay"])

                    timeout {
                        requestTimeoutMillis = 1000
                    }
                }

                call.respondText(response.body())
            } catch (cause: Throwable) {
                call.respond(HttpStatusCode.GatewayTimeout, cause.message!!)
            }
        }
    }
}

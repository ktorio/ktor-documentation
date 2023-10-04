package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.callid.CallId as ClientCallId
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val client = HttpClient(CIO) {
        install(ClientCallId)
    }
    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { "call-id-server" }
        header(HttpHeaders.XRequestId)
    }
    routing {
        get("/") {
            call.respond("Hello World")
        }
        get("/double-call") {
            val response = client.get("http://0.0.0.0:8090/call")
            call.respond(response.bodyAsText())
        }
        get("/call") {
            val callIdFromCall = call.callId
            val callIdFromHeader = call.request.headers[HttpHeaders.XRequestId]
            val callIdFromParameter = call.request.queryParameters["callId"]
            call.respond("in call: $callIdFromCall, in header: $callIdFromHeader, in query param: $callIdFromParameter")
        }
    }

}
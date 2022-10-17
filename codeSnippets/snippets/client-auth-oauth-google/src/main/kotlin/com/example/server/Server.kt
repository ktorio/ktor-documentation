package com.example.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Starts a local web server used to get the authorization code as the query parameter
 * returned as a part of the redirect URI.
 * Learn more at [Loopback IP Address flow](https://developers.google.com/identity/protocols/oauth2/resources/loopback-migration).
 */
fun startServer() = embeddedServer(Netty, port = 8080) {
    routing {
        get("/") {
            call.respondText("Authorization code: ${call.request.queryParameters["code"]}")
        }
    }
}.start()
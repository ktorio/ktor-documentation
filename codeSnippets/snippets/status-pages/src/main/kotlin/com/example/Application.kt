package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if(cause is AuthorizationException) {
                call.respondText(text = "403: $cause" , status = HttpStatusCode.Forbidden)
            } else {
                call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
            }
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page Not Found", status = status)
        }
        statusFile(HttpStatusCode.Unauthorized, HttpStatusCode.PaymentRequired, filePattern = "error#.html")
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
        get("/internal-error") {
            throw Exception("Internal Server Error")
        }
        get("/authorization-error") {
            throw AuthorizationException("Forbidden Error")
        }
        get("/authentication-error") {
            call.respond(HttpStatusCode.Unauthorized)
        }
        get("/payment-error") {
            call.respond(HttpStatusCode.PaymentRequired)
        }
    }
}

class AuthorizationException(override val message: String?) : Throwable()

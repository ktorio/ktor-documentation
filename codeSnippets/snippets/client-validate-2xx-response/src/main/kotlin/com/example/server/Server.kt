package com.example.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jsonkotlinx.*
import kotlinx.serialization.*

/**
 * Starts a local web server.
 */
fun startServer() = embeddedServer(Netty, port = 8080, module = Application::module).start()

fun Application.module() {
    main()
    testModule()
}

fun Application.testModule() {
    @Serializable
    data class Error(val code: Int, val message: String)

    val error = Error(3, "Some custom error")
    routing {
        get("/error") {
            call.response.status(HttpStatusCode.InternalServerError)
            call.respond(error)
        }
    }
}

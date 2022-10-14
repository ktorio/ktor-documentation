package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Serializable
data class Car(val type: String, val model: String, val color: String)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/json") {
            call.respond(Car("Fiat", "500", "white"))
        }
        get("/file") {
            val file = File("public/ktor_logo.png")
            call.respondFile(file)
        }
        get("/file-attachment") {
            val file = File("public/ktor_logo.png")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor_logo.png")
                    .toString()
            )
            call.respondFile(file)
        }
        get("/old") {
            call.respondRedirect("/moved", permanent = true)
        }
        get("/moved") {
            call.respondText("Moved resource")
        }
    }
}

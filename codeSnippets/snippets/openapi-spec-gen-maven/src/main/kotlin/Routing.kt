package com.example

import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger") {
            info = OpenApiInfo(title = "My API", version = "1.0.0")
        }

        route("/api") {
            route("/messages") {
                /**
                 * Get a list of messages
                 */
                get {
                    call.respond(listOf(Message(1, "Hello World!")))
                }
                /**
                 * Get a single message by ID
                 */
                get("{id}") {
                    call.respond(Message(call.parameters["id"]!!.toLong(), "Hello World!"))
                }
                /**
                 * Create a new message
                 */
                post {
                    call.respond(Message(1, call.receive<String>()))
                }
                /**
                 * Delete a message by ID
                 */
                delete("{id}") {
                    call.respond(HttpStatusCode.NoContent)
                }
            }

        }
    }
}
@Serializable
data class Message(
    val id: Long,
    val text: String
)
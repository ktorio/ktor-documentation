package com.example

import io.ktor.annotate.annotate
import io.ktor.http.*
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        /**
         * Hello, world.
         *
         * @response 200 text/plaintext Hello
         */
        get("/hello") {
            call.respondText("Hello")
        }

        /**
         * Data back-end.
         */
        route("/data") {

            /**
             * Users endpoint.
             */
            route("/users") {
                val list = mutableListOf<User>()

                /**
                 * Get a single user by ID.
                 *
                 * @path id [ULong] the ID of the user
                 * @response 400 The ID parameter is malformatted or missing.
                 * @response 404 The user for the given ID does not exist.
                 * @response 200 [User] The user found with the given ID.
                 */
                get("/{id}") {
                    val id = call.parameters["id"]?.toULongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val user = list.find { it.id == id }
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(user)
                }

                /**
                 * Get a list of users.
                 *
                 * @response 200 The list of items.
                 */
                get("/users") {
                    val query = call.parameters["q"]
                    val result = if (query != null) {
                        list.filter {it.name.contains(query, ignoreCase = true)  }
                    } else {
                        list
                    }

                    call.respond(result)
                }.annotate {
                    summary = "Get users"
                    description = "Retrieves a list of users."
                    parameters {
                        query("q") {
                            description = "An encoded query"
                            required = false
                        }
                    }
                    responses {
                        HttpStatusCode.OK {
                            description = "A list of users"
                            schema = jsonSchema<List<User>>()
                        }
                        HttpStatusCode.BadRequest {
                            description = "Invalid query"
                            ContentType.Text.Plain()
                        }
                    }
                }

                /**
                 * Save a new user.
                 *
                 * @response 204 The new user was saved.
                 */
                post {
                    list += call.receive<User>()
                    call.respond(HttpStatusCode.NoContent)
                }

                /**
                 * Delete the user with the given ID.
                 *
                 * @path id [ULong] the ID of the user to remove
                 * @response 400 The ID parameter is malformatted or missing.
                 * @response 404 The user for the given ID does not exist.
                 * @response 204 The user was deleted.
                 */
                delete("/{id}") {
                    val id = call.parameters["id"]?.toULongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    if (!list.removeIf { it.id == id })
                        return@delete call.respond(HttpStatusCode.NotFound)
                    call.respond(HttpStatusCode.NoContent)
                }

            }
        }
        openAPI(
            path = "/docs",
            swaggerFile = "openapi/generated.json"
        )
    }
}

@Serializable
data class User(val id: ULong, val name: String)

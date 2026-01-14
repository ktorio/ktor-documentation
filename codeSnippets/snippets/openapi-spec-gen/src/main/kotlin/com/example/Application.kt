package com.example

import io.ktor.annotate.OpenApiDocSource
import io.ktor.annotate.annotate
import io.ktor.annotate.generateOpenApiDoc
import io.ktor.http.*
import io.ktor.openapi.OpenApiDoc
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
        json(Json {
            encodeDefaults = false
        })
    }
    routing {
        // Main page for marketing
        get("/") {
            call.respondText("<html><body><h1>Hello, World</h1></body></html>", ContentType.Text.Html)
        }

        /**
         * API endpoints for users.
         *
         * These will appear in the resulting OpenAPI document.
         */
        val apiRoute = userCrud()

        get("/docs.json") {
            val docs = generateOpenApiDoc(
                OpenApiDoc(info = OpenApiInfo("My API", "1.0")),
                apiRoute.descendants()
            )
            call.respond(docs)
        }

        /**
         * View the generated UI for the API spec.
         */
        openAPI("/openApi"){
            outputPath = "docs/routes"
            // title, version, etc.
            info = OpenApiInfo("My API from routes", "1.0.0")
            // which routes to read from to build the model
            // by default, it will check for `openapi/documentation.yaml` then use the routing root as a fallback
            source = OpenApiDocSource.RoutingSource(ContentType.Application.Json) {
                apiRoute.descendants()
            }
        }

        /**
         * View the Swagger flavor of the UI for the API spec.
         */
        swaggerUI("/swaggerUI") {
            info = OpenApiInfo("My API", "1.0")
            source = OpenApiDocSource.RoutingSource(ContentType.Application.Json) {
                apiRoute.descendants()
            }
        }
    }
}

fun Routing.userCrud(): Route =
    route("/api") {
        route("/users") {
                val list = mutableListOf<User>()

                /**
                 * Get a single user by ID.
                 *
                 * – Path: id [ULong] the ID of the user
                 * – Response: 400 The ID parameter is malformatted or missing.
                 * – Response: 404 The user for the given ID does not exist.
                 * – Response: 200 [User] The user found with the given ID.
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
                 * – Response: 200 The list of items.
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
                 * – Response: 204 The new user was saved.
                 */
                post {
                    list += call.receive<User>()
                    call.respond(HttpStatusCode.NoContent)
                }

                /**
                 * Delete the user with the given ID.
                 *
                 * – Path id [ULong] the ID of the user to remove
                 * – Response: 400 The ID parameter is malformatted or missing.
                 * – Response: 404 The user for the given ID does not exist.
                 * – Response: 204 The user was deleted.
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

@Serializable
data class User(val id: ULong, val name: String)

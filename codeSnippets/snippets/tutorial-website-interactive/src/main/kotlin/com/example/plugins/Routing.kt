package com.example.plugins

import com.example.models.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
    routing {
        static("/static") {
            resources("files")
        }
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("microposts" to microposts)))
        }

        route("microposts") {
            get {
                call.respond(FreeMarkerContent("view.ftl", mapOf("microposts" to microposts)))
            }
            get("new") {
                call.respond(FreeMarkerContent("new.ftl", model = null))
            }
            get("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("show.ftl", mapOf("micropost" to microposts.find { it.id == id })))
            }
            post {
                val params = call.receiveParameters()
                val action = params.getOrFail("action")
                when (action) {
                    "delete" -> {
                        val id = params.getOrFail("id")
                        microposts.removeIf { it.id.toString() == id }
                    }
                    "add" -> {
                        val headline = params.getOrFail("headline")
                        val body = params.getOrFail("body")
                        val newEntry = Micropost.newEntry(headline, body)
                        microposts.add(0, newEntry)
                    }
                }
                call.respondRedirect("/")
            }
            get("{id}/edit") {

            }
            patch("{id}") {

            }
            delete("{id}") {

            }
        }
    }
}

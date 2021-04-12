package com.example

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.application.*
import io.ktor.mustache.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
    routing {
        get("/index") {
            val sampleUser = User(1, "John")
            call.respond(MustacheContent("index.hbs", mapOf("user" to sampleUser)))
        }
    }
}

data class User(val id: Int, val name: String)

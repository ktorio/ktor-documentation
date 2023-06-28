package com.example

import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.pebbletemplates.pebble.loader.ClasspathLoader

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates"
        })
    }
    routing {
        get("/index") {
            val sampleUser = User(1, "John")
            call.respond(PebbleContent("index.html", mapOf("user" to sampleUser)))
        }
    }
}

data class User(val id: Int, val name: String)

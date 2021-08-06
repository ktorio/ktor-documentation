package com.example

import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.application.*
import io.ktor.pebble.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
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

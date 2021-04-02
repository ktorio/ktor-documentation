package com.example

import freemarker.cache.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        get("/index") {
            val sampleUser = User(1, "John")
            call.respond(FreeMarkerContent("index.ftl", mapOf("user" to sampleUser)))
        }
    }
}

data class User(val id: Int, val name: String)

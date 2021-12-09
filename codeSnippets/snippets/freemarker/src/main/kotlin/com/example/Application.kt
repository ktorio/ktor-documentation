package com.example

import freemarker.cache.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
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

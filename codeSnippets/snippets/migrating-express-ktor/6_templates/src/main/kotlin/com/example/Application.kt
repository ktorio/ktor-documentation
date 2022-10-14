package com.example

import freemarker.cache.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "views")
    }
    routing {
        get("/") {
            val article = Article("Hey", "Hello there!")
            call.respond(FreeMarkerContent("index.ftl", mapOf("article" to article)))
        }
    }
}

data class Article(val title: String, val message: String)

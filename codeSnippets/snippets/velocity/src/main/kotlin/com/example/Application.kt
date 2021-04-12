package com.example

import io.ktor.application.*
import io.ktor.velocity.*
import io.ktor.response.*
import io.ktor.routing.*
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.apache.velocity.runtime.RuntimeConstants

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(Velocity) {
        setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
    }
    routing {
        get("/index") {
            val sampleUser = User(1, "John")
            call.respond(VelocityContent("templates/index.vl", mapOf("user" to sampleUser)))
        }
    }
}

data class User(val id: Int, val name: String)

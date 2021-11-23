package com.example

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*

@Serializable
@Resource("/")
class Index

@Serializable
@Resource("/")
class IndexLocal

@Serializable
@Resource("/user/{id}")
class User(val id: Int)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(Resources)
    routing {
        get<Index> {
            call.respondText("Hello from index")
        }

        get<IndexLocal> {
            call.respondText("Hello from index local")
        }

        get<User> { user ->
            call.respondText("Hello from ${user.id}")
        }
    }
}

package com.example

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(
    greetingService: GreetingService,
    userRepository: UserRepository,
) {
    routing {
        val optional: OptionalConfig? by dependencies

        get("/greet/{name}") {
            val name = call.parameters["name"] ?: "World"
            call.respondText(greetingService.greet(name))
        }

        get("/db") {
            call.respondText("DB = ${userRepository.db}")
        }

        get("/optional") {
            call.respondText("Optional = $optional")
        }
    }
}

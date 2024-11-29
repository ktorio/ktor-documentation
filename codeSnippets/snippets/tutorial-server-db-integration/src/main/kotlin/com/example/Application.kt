package com.example

import io.ktor.server.application.*
import com.example.model.PostgresTaskRepository

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = PostgresTaskRepository()

    configureSerialization(repository)
    configureDatabases()
    configureRouting()
}
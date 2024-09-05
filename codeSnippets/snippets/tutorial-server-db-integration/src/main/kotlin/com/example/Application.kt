package com.example

import com.example.model.FakeTaskRepository
import com.example.plugins.*
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

fun Application.testModule() {
    val repository = FakeTaskRepository()

    configureSerialization(repository)
    configureRouting()
}
package com.example

import com.example.model.PostgresTaskRepository
import com.example.model.TaskRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.configureDependencies() {
    dependencies {
        provide<TaskRepository> {
            PostgresTaskRepository()
        }
    }
}
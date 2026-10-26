package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import com.example.model.PostgresTaskRepository
import com.example.model.TaskRepository

// The contents of the `install` function will be used for the project template
fun Application.configureDependencyInjection() {
    dependencies {
        provide<TaskRepository> {
            PostgresTaskRepository()
        }
    }
}
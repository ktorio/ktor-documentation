package com.example

import com.example.model.PostgresTaskRepository
import com.example.model.TaskRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

// The contents of the `install` function will be used for the project template
fun Application.configureDependencyInjection() {
    dependencies {
        provide<TaskRepository> {
            PostgresTaskRepository()
        }
    }
}

package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "postgres",
        password = "password"
    )
}
package com.example

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
val hashedUserTable = UserHashedTableAuth(
    table = mapOf(
        "jetbrains" to digestFunction("foobar"),
        "admin" to digestFunction("password")
    ),
    digester = digestFunction
)

fun Application.main() {
    install(Authentication) {
        basic("auth-basic-hashed") {
            realm = "Access to the '/' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }
    routing {
        authenticate("auth-basic-hashed") {
            get("/") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}

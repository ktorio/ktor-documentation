package com.example

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                if(credentials.name == "jetbrains" && credentials.password == "foobar")
                    UserIdPrincipal(credentials.name)
                else null
            }
        }
    }
    routing {
        authenticate("auth-form") {
            post("/") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}

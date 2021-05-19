package com.example

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.ldap.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    install(Authentication) {
        basic("auth-ldap") {
            validate { credentials ->
                ldapAuthenticate(credentials, "ldap://0.0.0.0:389", "cn=%s,dc=ktor,dc=io")
            }
        }
    }
    routing {
        authenticate("auth-ldap") {
            get("/") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}

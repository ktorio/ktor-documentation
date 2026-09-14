package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.ldap.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.main() {
    install(Authentication) {
        basic("auth-ldap") {
            validate { credentials ->
                val url = "ldap://0.0.0.0:389"
                val userDNFormat = "cn=%s,dc=ktor,dc=io"
                ldapAuthenticate(credentials, url, userDNFormat)
            }
        }
    }
    routing {
        authenticate("auth-ldap") {
            get("/") {
                val user = call.principal<UserIdPrincipal>()
                call.respondText("Hello, ${user?.name}!")
            }
        }
    }
}

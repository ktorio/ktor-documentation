package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val isValid = credentials.name == "jetbrains" &&
                    credentials.password == "foobar"
                if (isValid) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
            challenge {
                val message = "Credentials are not valid"
                val status = HttpStatusCode.Unauthorized
                call.respond(status, message)
            }
        }
    }

    routing {
        authenticate("auth-form") {
            post("/login") {
                val user = call.principal<UserIdPrincipal>()
                call.respondText("Hello, ${user?.name}!")
            }
        }

        get("/login") {
            call.respondHtml {
                body {
                    val urlEncoded = FormEncType
                        .applicationXWwwFormUrlEncoded
                    form(
                        action = "/login",
                        encType = urlEncoded,
                        method = FormMethod.post
                    ) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Password:"
                            passwordInput(name = "password")
                        }
                        p {
                            submitInput() { value = "Login" }
                        }
                    }
                }
            }
        }
    }
}

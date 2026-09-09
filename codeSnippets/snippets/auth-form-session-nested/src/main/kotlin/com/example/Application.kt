package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val name: String, val count: Int)

fun Application.main() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }
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
        }
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.name.startsWith("jet")) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
        basic("auth-basic") {
            realm = "Access to the '/admin' path"
            validate { credentials ->
                val isValid = credentials.name == "admin" &&
                    credentials.password == "password"
                if (isValid) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    routing {
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

        authenticate("auth-form") {
            post("/login") {
                val principal =
                    call.principal<UserIdPrincipal>()
                val userName = principal?.name.toString()
                val session = UserSession(
                    name = userName,
                    count = 1
                )
                call.sessions.set(session)
                call.respondRedirect("/hello")
            }
        }

        val required = AuthenticationStrategy.Required
        authenticate("auth-session", strategy = required) {
            get("/hello") {
                val userSession = call.principal<UserSession>()
                val next = userSession?.copy(
                    count = userSession.count + 1
                )
                call.sessions.set(next)
                call.respondText(
                    "Hello, ${userSession?.name}! " +
                        "Visit count is ${userSession?.count}."
                )
            }
            authenticate("auth-basic", strategy = required) {
                get("/admin") {
                    val userSession =
                        call.principal<UserSession>(
                            "auth-session"
                        )
                    call.respondText(
                        "Hi, ${userSession?.name}! " +
                            "Welcome to the Admin page."
                    )
                }
            }
        }

        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }
    }
}

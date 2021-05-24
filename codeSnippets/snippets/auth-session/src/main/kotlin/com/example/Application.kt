package com.example

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*

data class UserSession(val id: String) : Principal

fun Application.main() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.id.startsWith("123")) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
    routing {
        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/login") { +"Login" }
                    }
                }
            }
        }

        get("/login") {
            call.sessions.set(UserSession("123abc"))
            call.respondRedirect("/hello")
        }

        authenticate("auth-session") {
            get("/hello") {
                call.respondHtml {
                    body {
                        p {
                            +"Session ID is ${call.principal<UserSession>()?.id}"
                        }
                        p {
                            a("/logout") { +"Logout" }
                        }
                    }
                }
            }
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
    }
}

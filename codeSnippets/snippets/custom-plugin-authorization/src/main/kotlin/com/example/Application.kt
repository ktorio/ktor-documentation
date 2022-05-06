package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun getUserRole(userName: String?): String {
    val userRole = when (userName) {
        "jetbrains" -> "user"
        "admin" -> "admin"
        else -> ""
    }
    return userRole
}

fun Application.module() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "jetbrains" ||
                    credentials.name == "admin" &&
                    credentials.password.isNotEmpty()
                ) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        authenticate("auth-basic") {
            route("/admin") {
                install(AuthorizationPlugin) {
                    roles = setOf("admin")
                    getRole = ::getUserRole
                }
                get {
                    call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
                }
            }
            route("/profile") {
                install(AuthorizationPlugin) {
                    roles = setOf("user", "admin")
                    getRole = ::getUserRole
                }
                get {
                    call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
                }
            }
        }
    }
}

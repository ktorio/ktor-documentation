package com.example

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*

fun Application.main() {
    routing {
        post("/signup") {
            val formParameters = call.receiveParameters()
            val username = formParameters["username"].toString()
            call.respondText("The '$username' account is created")
        }

        get("/") {
            call.respondHtml {
                body {
                    form(action = "/signup", encType = FormEncType.applicationXWwwFormUrlEncoded, method = FormMethod.post) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Email:"
                            textInput(name = "email")
                        }
                        p {
                            +"Password:"
                            passwordInput(name = "password")
                        }
                        p {
                            +"Confirmation:"
                            passwordInput(name = "confirmation")
                        }
                        p {
                            submitInput() { value = "Create my account" }
                        }
                    }
                }
            }
        }
    }
}

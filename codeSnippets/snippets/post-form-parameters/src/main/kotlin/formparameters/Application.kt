package formparameters

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

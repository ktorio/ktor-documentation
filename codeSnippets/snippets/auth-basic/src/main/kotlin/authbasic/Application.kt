package authbasic

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.main() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
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
    }
    routing {
        authenticate("auth-basic") {
            get("/") {
                val user = call.principal<UserIdPrincipal>()
                call.respondText("Hello, ${user?.name}!")
            }
        }
    }
}

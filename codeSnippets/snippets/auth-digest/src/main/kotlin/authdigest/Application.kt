package authdigest

import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.text.Charsets.UTF_8

val myRealm = "Access to the '/' path"
val userPasswords: Map<String, String> = mapOf(
    "jetbrains" to "foobar",
    "admin" to "password"
)

fun computeHash(userName: String, realm: String, password: String, algorithm: DigestAlgorithm): ByteArray =
    algorithm.toDigester().digest("$userName:$realm:$password".toByteArray(UTF_8))

fun Application.main() {
    install(Authentication) {
        digest("auth-digest") {
            realm = myRealm
            // Support both modern SHA-512-256 and legacy MD5 clients
            algorithms = listOf(DigestAlgorithm.SHA_512_256, DigestAlgorithm.MD5)
            digestProvider { userName, realm, algorithm ->
                // Compute H(username:realm:password) using the requested algorithm
                userPasswords[userName]?.let { password ->
                    computeHash(userName, realm, password, algorithm)
                }
            }
            validate { credentials ->
                if (credentials.userName.isNotEmpty()) {
                    CustomPrincipal(credentials.userName, credentials.realm)
                } else {
                    null
                }
            }
        }
    }
    routing {
        authenticate("auth-digest") {
            get("/") {
                call.respondText("Hello, ${call.principal<CustomPrincipal>()?.userName}!")
            }
        }
    }
}

data class CustomPrincipal(val userName: String, val realm: String)

package com.example

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*
import java.util.*

@Serializable
data class User(val username: String, val password: String)

fun Application.main() {
    install(ContentNegotiation) {
        json()
    }
    val jwtConfig = environment.config
    val secret = jwtConfig.property("jwt.secret").getString()
    val issuer = jwtConfig.property("jwt.issuer").getString()
    val audience = jwtConfig
        .property("jwt.audience").getString()
    val myRealm = jwtConfig.property("jwt.realm").getString()
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build())
            validate { credential ->
                val payload = credential.payload
                val claim = payload.getClaim("username")
                if (claim.asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                val text = "Token is not valid or has expired"
                val status = HttpStatusCode.Unauthorized
                call.respond(status, text)
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<User>()
            // Check username and password
            // ...
            val expiresAt = System.currentTimeMillis() + 60000
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(expiresAt))
                .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val payload = principal!!.payload
                val username = payload.getClaim("username")
                    .asString()
                val now = System.currentTimeMillis()
                val expiresAt = principal.expiresAt?.time
                    ?.minus(now)
                call.respondText(
                    "Hello, $username! " +
                        "Token is expired at $expiresAt ms."
                )
            }
        }
    }
}

package com.example

import com.auth0.jwk.*
import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import java.io.*
import java.security.*
import java.security.interfaces.*
import java.security.spec.*
import java.util.*
import java.util.concurrent.*

@Serializable
data class User(val username: String, val password: String)

fun Application.main() {
    install(ContentNegotiation) {
        json()
    }
    val jwtConfig = environment.config
    val privateKeyString = jwtConfig
        .property("jwt.privateKey").getString()
    val issuer = jwtConfig.property("jwt.issuer").getString()
    val audience = jwtConfig
        .property("jwt.audience").getString()
    val myRealm = jwtConfig.property("jwt.realm").getString()
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }
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
            val keyId = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
            val publicKey = jwkProvider.get(keyId).publicKey
            val decoded = Base64.getDecoder()
                .decode(privateKeyString)
            val keySpecPKCS8 = PKCS8EncodedKeySpec(decoded)
            val privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(keySpecPKCS8)
            val algorithm = Algorithm.RSA256(
                publicKey as RSAPublicKey,
                privateKey as RSAPrivateKey
            )
            val expiresAt = System.currentTimeMillis() + 60000
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(expiresAt))
                .sign(algorithm)
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
        staticFiles(".well-known", File("certs"), "jwks.json")
    }
}

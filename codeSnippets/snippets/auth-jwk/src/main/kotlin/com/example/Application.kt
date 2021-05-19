package com.example

import com.auth0.jwk.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File
import java.util.concurrent.*

fun Application.main() {
    val jwkIssuer = environment.config.property("jwt.domain").getString()
    val jwkAudience = environment.config.property("jwt.audience").getString()
    val jwkRealm = environment.config.property("jwt.realm").getString()
    val jwkProvider = JwkProviderBuilder(jwkIssuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth-jwk") {
            realm = jwkRealm
            verifier(jwkProvider, jwkIssuer)
            validate { credential ->
                if (credential.payload.audience.contains(jwkAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    routing {
        authenticate("auth-jwk") {
            get("/") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val subjectString = principal!!.payload.subject.removePrefix("auth0|")
                call.respondText("Hello, $subjectString!")
            }
        }
        static(".well-known") {
            staticRootFolder = File("certs")
            file("jwks.json")
        }
    }
}


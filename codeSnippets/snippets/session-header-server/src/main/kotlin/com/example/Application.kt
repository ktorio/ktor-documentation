package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import java.io.*

@Serializable
data class CartSession(val userID: String, val productIDs: MutableList<Int>)

fun Application.main() {
    install(Sessions) {
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        header<CartSession>("cart_session", directorySessionStorage(File("build/.sessions"))) {
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
    routing {
        get("/login") {
            call.sessions.set(CartSession(userID = "123", productIDs = mutableListOf(1, 3, 7)))
            call.respondText("Successful login")
        }

        get("/cart") {
            val cartSession = call.sessions.get<CartSession>()
            if (cartSession != null) {
                call.respondText("Product IDs: ${cartSession.productIDs}")
            } else {
                call.respondText("Your basket is empty.")
            }
        }

        get("/logout") {
            call.sessions.clear<CartSession>()
            call.respondText("Successful logout")
        }
    }
}

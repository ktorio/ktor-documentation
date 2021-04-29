package com.example

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

fun Application.main() {
    val httpClient = HttpClient(CIO) {
        install(JsonFeature)
    }
    install(Authentication) {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = httpClient
        }
    }
    routing {
        var principal: OAuthAccessTokenResponse.OAuth2? = null
        authenticate("auth-oauth-google") {
            get("/callback") {
                principal = call.authentication.principal()
                call.respondRedirect("/hello")
            }
        }
        get("/") {
            call.respondRedirect("/callback")
        }
        get("/hello") {
            val userInfo: UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${principal?.accessToken}")
                }
            }
            call.respondText("Hello, ${userInfo.name}!")
        }
    }
}

data class UserInfo(
    val id: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val locale: String
)

package com.example

import com.example.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        // Step 1: Get an authorization code
        val authorizationUrlQuery = Parameters.build {
            append("client_id", System.getenv("GOOGLE_CLIENT_ID"))
            append("scope", "https://www.googleapis.com/auth/userinfo.profile")
            append("response_type", "code")
            append("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
            append("access_type", "offline")
        }.formUrlEncode()
        println("https://accounts.google.com/o/oauth2/auth?$authorizationUrlQuery")
        println("Open a link above, get the authorization code, insert it below, and press Enter.")
        val authorizationCode = readln()

        // Step 2: Create a storage for tokens
        val bearerTokenStorage = mutableListOf<BearerTokens>()

        // Step 3: Configure the client for receiving tokens and accessing the protected API
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        bearerTokenStorage.last()
                    }
                    refreshTokens {
                        val refreshTokenInfo: TokenInfo = client.submitForm(
                            url = "https://accounts.google.com/o/oauth2/token",
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("client_id", System.getenv("GOOGLE_CLIENT_ID"))
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                            }
                        ).body()
                        bearerTokenStorage.add(BearerTokens(refreshTokenInfo.accessToken, oldTokens?.refreshToken!!))
                        bearerTokenStorage.last()
                    }
                    sendWithoutRequest { request ->
                        request.url.host == "www.googleapis.com"
                    }
                }
            }
        }

        // Step 4: Exchange the authorization code for tokens and save tokens in the storage
        val tokenInfo: TokenInfo = client.submitForm(
            url = "https://accounts.google.com/o/oauth2/token",
            formParameters = Parameters.build {
                append("grant_type", "authorization_code")
                append("code", authorizationCode)
                append("client_id", System.getenv("GOOGLE_CLIENT_ID"))
                append("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
            }
        ).body()
        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken!!))

        // Step 5: Make a request to the protected API
        while (true) {
            println("Make a request? Type 'yes' and press Enter to proceed.")
            when (readln()) {
                "yes" -> {
                    val response: HttpResponse = client.get("https://www.googleapis.com/oauth2/v2/userinfo")
                    try {
                        val userInfo: UserInfo = response.body()
                        println("Hello, ${userInfo.name}!")
                    } catch (e: Exception) {
                        val errorInfo: ErrorInfo = response.body()
                        println(errorInfo.error.message)
                    }
                }
                else -> return@runBlocking
            }
        }
    }
}

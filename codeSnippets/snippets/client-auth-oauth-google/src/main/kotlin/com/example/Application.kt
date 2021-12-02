package com.example

import com.example.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import java.util.*

fun main() {
    runBlocking {
        // Step 1: Create a storage for tokens
        val bearerTokenStorage = mutableListOf<BearerTokens>()

        // Step 2: Get an authorization code
        val authorizationUrlQuery = Parameters.build {
            append("client_id", System.getenv("GOOGLE_CLIENT_ID"))
            append("scope", "https://www.googleapis.com/auth/userinfo.profile")
            append("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
            append("response_type", "code")
            append("access_type", "offline")
        }.formUrlEncode()
        println("https://accounts.google.com/o/oauth2/auth?$authorizationUrlQuery")
        println("Open a link above, get the authorization code, insert it below, and press Enter.")
        val input = Scanner(System.`in`)
        val authorizationCode = input.next()

        // Step 3: Exchange the authorization code for tokens and save tokens in the storage
        val tokenClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val tokenInfo: TokenInfo = tokenClient.submitForm(
            url = "https://accounts.google.com/o/oauth2/token",
            formParameters = Parameters.build {
                append("grant_type", "authorization_code")
                append("code", authorizationCode)
                append("client_id", System.getenv("GOOGLE_CLIENT_ID"))
                append("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
            }
        ).body()
        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken!!))

        // Step 4: Configure the client for accessing the protected API
        val apiClient = HttpClient(CIO) {
            expectSuccess = false
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
                }
            }
        }

        // Step 5: Make a request to the protected API
        while (true) {
            println("Make a request? Type 'yes' and press Enter to proceed.")
            val input = Scanner(System.`in`)
            when (input.next()) {
                "yes" -> {
                    val response: HttpResponse = apiClient.get("https://www.googleapis.com/oauth2/v2/userinfo")
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

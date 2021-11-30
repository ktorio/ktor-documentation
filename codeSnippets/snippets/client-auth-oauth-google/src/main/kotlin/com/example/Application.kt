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
import kotlinx.serialization.*
import java.util.*

val scope = "https://www.googleapis.com/auth/userinfo.profile"
val redirectUri = "urn:ietf:wg:oauth:2.0:oob"
val clientId = System.getenv("GOOGLE_CLIENT_ID")

fun main() {
    runBlocking {
        // Step 1: Get an authorization code
        val authorizationCode = getAuthorizationCode()

        // Step 2: Exchange the authorization code for tokens
        val tokenClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val tokenInfo = getTokenInfo(tokenClient, authorizationCode)
        accessTokenStorage.add(tokenInfo)

        println(tokenInfo.accessToken)

        // Step 3: Configure the client used to access the protected API
        val apiClient = HttpClient(CIO) {
            expectSuccess = false
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = accessTokenStorage.last().accessToken,
                            refreshToken = accessTokenStorage.last().refreshToken ?: ""
                        )
                    }

                    refreshTokens {
                        val refreshTokenInfo: TokenInfo = client.submitForm(
                            url = "https://accounts.google.com/o/oauth2/token",
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("client_id", clientId)
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                            }
                        ).body()
                        accessTokenStorage.add(refreshTokenInfo)
                        BearerTokens(
                            accessToken = accessTokenStorage.last().accessToken,
                            refreshToken = oldTokens?.refreshToken ?: ""
                        )
                    }
                }
            }
        }

        // Step 4: Make a request to the protected API
        while(true) {
            println("Make a request? Type 'yes' to proceed.")
            val input = Scanner(System.`in`)
            when(input.next()) {
                "yes" -> {
                    val response: HttpResponse = apiClient.get("https://www.googleapis.com/oauth2/v2/userinfo")
                    println(response.bodyAsText())
                    println(response.status)
/*                    val userInfo: UserInfo = apiClient.get("https://www.googleapis.com/oauth2/v2/userinfo").body()
                    println("Hello, ${userInfo.name}!")*/
                }
                "test" -> {
                    val response: HttpResponse = apiClient.get("http://localhost:8080/customer/1")
                    println(response.bodyAsText())
                    println(response.status)
                }
                else -> return@runBlocking
            }
        }
    }
}

suspend fun getAuthorizationCode(): String {
    val authorizationUrlQuery = Parameters.build {
        append("client_id", clientId)
        append("scope", scope)
        append("redirect_uri", redirectUri)
        append("response_type", "code")
        append("access_type", "offline")
    }.formUrlEncode()
    println("https://accounts.google.com/o/oauth2/auth?$authorizationUrlQuery")
    println("Open a link above, get the authorization code, insert it below, and press Enter.")
    val input = Scanner(System.`in`)
    return input.next()
}

suspend fun getTokenInfo(tokenClient: HttpClient, authorizationCode: String): TokenInfo {
    return tokenClient.submitForm(
        url = "https://accounts.google.com/o/oauth2/token",
        formParameters = Parameters.build {
            append("grant_type", "authorization_code")
            append("code", authorizationCode)
            append("client_id", clientId)
            append("redirect_uri", redirectUri)
        }
    ).body()
}

package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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
        val authorizationCode = input.next()

        val tokenClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val apiClient = HttpClient(CIO) {
            expectSuccess = false
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                lateinit var tokenInfo: TokenInfo
                var refreshTokenInfo: TokenInfo

                bearer {
                    loadTokens {
                        tokenInfo = tokenClient.submitForm(
                            url = "https://accounts.google.com/o/oauth2/token",
                            formParameters = Parameters.build {
                                append("grant_type", "authorization_code")
                                append("code", authorizationCode)
                                append("client_id", clientId)
                                append("redirect_uri", redirectUri)
                            }
                        ).body()
                        BearerTokens(
                            accessToken = tokenInfo.accessToken,
                            refreshToken = tokenInfo.refreshToken!!
                        )
                    }

                    refreshTokens {
                        refreshTokenInfo = tokenClient.submitForm(
                            url = "https://accounts.google.com/o/oauth2/token",
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("client_id", clientId)
                                append("refresh_token", tokenInfo.refreshToken!!)
                            }
                        ).body()
                        BearerTokens(
                            accessToken = refreshTokenInfo.accessToken,
                            refreshToken = tokenInfo.refreshToken!!
                        )
                    }
                }
            }
        }

        val userInfo: UserInfo = apiClient.get("https://www.googleapis.com/oauth2/v2/userinfo").body()
        println("Hello, ${userInfo.name}!")
    }
}

@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String? = null,
    val scope: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("id_token") val idToken: String,
)

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String,
    val locale: String
)

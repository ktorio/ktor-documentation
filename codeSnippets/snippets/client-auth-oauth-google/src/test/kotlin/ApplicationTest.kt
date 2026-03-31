package com.example

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {
    @Test
    fun testSingleAuthProviderUsedDespiteDifferentAuthenticateHeader() = runBlocking {
        var firstRequest = true

        val engine = MockEngine { request ->
            val authHeader = request.headers[HttpHeaders.Authorization]

            if (firstRequest) {
                firstRequest = false
                respond(
                    content = "",
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(
                        HttpHeaders.WWWAuthenticate,
                        "Basic realm=\"Test\""
                    )
                )
            } else {
                assertEquals("Bearer valid", authHeader)
                respond(
                    content = "OK",
                    status = HttpStatusCode.OK
                )
            }
        }

        val client = HttpClient(engine) {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens("invalid", "refresh")
                    }

                    refreshTokens {
                        BearerTokens("valid", "refresh")
                    }
                }
            }
        }
        val response = client.get("https://test.example")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

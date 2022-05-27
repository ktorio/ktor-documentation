package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.INFO
            }
        }

        // Configure request URL
        val response: HttpResponse = client.get("https://ktor.io/docs/welcome.html")
        client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "ktor.io"
                path("docs/welcome.html")
            }
        }
        client.get("https://ktor.io") {
            url {
                appendPathSegments("docs", "welcome.html")
            }
        }
        client.get("https://ktor.io") {
            url {
                parameters.append("token", "abc123")
            }
        }
        client.get("https://ktor.io") {
            url {
                fragment = "some_anchor"
            }
        }

        // Add headers
        client.get("https://ktor.io") {
            headers {
                append(HttpHeaders.Accept, "text/html")
                append(HttpHeaders.Authorization, "abc123")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }

        // Add cookies
        client.get("https://ktor.io") {
            cookie(name = "user_name", value = "jetbrains", expires = GMTDate(
                seconds = 0,
                minutes = 0,
                hours = 10,
                dayOfMonth = 1,
                month = Month.APRIL,
                year = 2023
            ))
        }

        client.close()
    }
}

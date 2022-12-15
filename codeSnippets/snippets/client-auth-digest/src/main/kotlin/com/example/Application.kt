package com.example

import authdigest.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(Auth) {
                digest {
                    credentials {
                        DigestAuthCredentials(username = "jetbrains", password = "foobar")
                    }
                    realm = "Access to the '/' path"
                }
            }
        }
        val response: HttpResponse = client.get("http://0.0.0.0:8080/")
        println(response.bodyAsText())
        client.close()
    }
}
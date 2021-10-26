package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.security.cert.*
import javax.net.ssl.*

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            defaultRequest {
                host = "0.0.0.0"
                port = 8443
                url {
                    protocol = URLProtocol.HTTPS
                }
                header("X-Custom-Header", "Hello")
            }
            engine {
                https {
                    trustManager = object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()
                        override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                        override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                    }
                }
            }
        }

        val response: HttpResponse = client.request {
            url(path = "/")
        }
        println(response.bodyAsText())
    }
}

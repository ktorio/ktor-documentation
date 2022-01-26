package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import java.io.*
import java.security.*
import javax.net.ssl.*

fun main() {
    runBlocking {
        val cioClient = HttpClient(CIO) {
            engine {
                https {
                    trustManager = SslSettings.getTrustManager()
                }
            }
        }

        val response: HttpResponse = cioClient.get("https://0.0.0.0:8443/")
        println(response.bodyAsText())
    }
}

object SslSettings {
    private val keyStoreFile = FileInputStream("keystore.jks")
    private val keyStorePassword = "foobar".toCharArray()

    fun getKeyStore(): KeyStore {
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(keyStoreFile, keyStorePassword)
        return keyStore
    }

    fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    fun getSslContext(): SSLContext? {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, getTrustManagerFactory()?.trustManagers, null)
        return sslContext
    }

    fun getTrustManager(): X509TrustManager {
        return getTrustManagerFactory()?.trustManagers?.first { it is X509TrustManager } as X509TrustManager
    }
}

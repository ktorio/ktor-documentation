package com.example

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.java.*
import io.ktor.client.engine.jetty.jakarta.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.network.tls.*
import kotlinx.coroutines.*
import org.eclipse.jetty.util.ssl.*
import java.io.*
import java.security.*
import javax.net.ssl.*

fun main() {
    runBlocking {
        val apacheClient = HttpClient(Apache5) {
            engine {
                sslContext = SslSettings.getSslContext()
            }
        }
        val javaClient = HttpClient(Java) {
            engine {
                config {
                    sslContext(SslSettings.getSslContext())
                }
            }
        }
        val jettyClient = HttpClient(Jetty) {
            engine {
                sslContextFactory = SslContextFactory.Client().apply {
                    sslContext = SslSettings.getSslContext()
                }
            }
        }
        val cioClient = HttpClient(CIO) {
            engine {
                https {
                    trustManager = SslSettings.getTrustManager()
                    addKeyStore(SslSettings.getKeyStore(), "foobar".toCharArray())
                }
            }
        }
        val androidClient = HttpClient(Android) {
            engine {
                sslManager = { httpsURLConnection ->
                    httpsURLConnection.sslSocketFactory = SslSettings.getSslContext()?.socketFactory
                }
            }
        }
        val okHttpClient = HttpClient(OkHttp) {
            engine {
                config {
                    sslSocketFactory(SslSettings.getSslContext()!!.socketFactory, SslSettings.getTrustManager())
                }
            }
        }
        val response: HttpResponse = cioClient.get("https://localhost:8443/")
        println(response.bodyAsText())
        cioClient.close()
    }
}

object SslSettings {
    fun getKeyStore(): KeyStore {
        val keyStoreFile = FileInputStream("../ssl-engine-main/keystore.jks")
        val keyStorePassword = "foobar".toCharArray()
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(keyStoreFile, keyStorePassword)
        return keyStore
    }

    fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    fun getKeyManagerFactory(): KeyManagerFactory? {
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(getKeyStore(), "foobar".toCharArray())
        return keyManagerFactory
    }

    fun getSslContext(): SSLContext? {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(getKeyManagerFactory()?.keyManagers, getTrustManagerFactory()?.trustManagers, null)
        return sslContext
    }

    fun getTrustManager(): X509TrustManager {
        return getTrustManagerFactory()?.trustManagers?.first { it is X509TrustManager } as X509TrustManager
    }
}

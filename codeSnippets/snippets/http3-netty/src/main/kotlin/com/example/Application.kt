package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.netty.http3.HmacQuicTokenHandler
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import java.io.FileInputStream
import java.security.KeyStore
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalKtorApi::class)
fun main(args: Array<String>) {
    val keyStore = KeyStore.getInstance("JKS").apply {
        FileInputStream("keystore.jks").use {
            load(it, "foobar".toCharArray())
        }
    }

    embeddedServer(
        Netty,
        configure = {
            sslConnector(
                keyStore = keyStore,
                keyAlias = "server",
                keyStorePassword = { "foobar".toCharArray() },
                privateKeyPassword = { "foobar".toCharArray() }
            ) {
                host = "0.0.0.0"
                port = 8443
            }
            /*
            enableHttp3()
             */
            enableHttp3 {
                quicTokenHandler = HmacQuicTokenHandler()
                quicMaxIdleTimeout = 30.seconds
                quicInitialMaxData = 10_000_000
                quicInitialMaxStreamDataBidirectionalLocal = 1_000_000
                quicInitialMaxStreamDataBidirectionalRemote = 1_000_000
                quicInitialMaxStreamsBidirectional = 100
                udpSocketCount = 1
                udpReceiveBufferSize = 0
                udpSendBufferSize = 0
            }
        }
    ){
        module()
    }.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello from Ktor!")
        }
    }
}
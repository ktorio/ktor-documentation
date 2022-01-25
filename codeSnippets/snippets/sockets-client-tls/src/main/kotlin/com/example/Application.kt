package com.example

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.security.cert.*
import javax.net.ssl.*

fun main() {
    runBlocking {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect("www.jetbrains.com", port = 443).tls(coroutineContext = coroutineContext) {
            trustManager = object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()
                override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
            }
        }
        val sendChannel = socket.openWriteChannel()
        val EOL = "\r\n"
        sendChannel.writeStringUtf8("GET / HTTP/1.1${EOL}Host: www.jetbrains.com${EOL}Connection: close${EOL}${EOL}")
        sendChannel.flush()
        println(socket.openReadChannel().readRemaining().readBytes().toString(Charsets.UTF_8))
    }
}

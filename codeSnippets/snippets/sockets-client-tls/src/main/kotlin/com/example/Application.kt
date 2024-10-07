package com.example

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.io.readByteArray
import java.security.cert.*
import javax.net.ssl.*

fun main() {
    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect("youtrack.jetbrains.com", port = 443).tls(coroutineContext = coroutineContext) {
            trustManager = object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()
                override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
            }
        }
        val sendChannel = socket.openWriteChannel()
        val EOL = "\r\n"
        sendChannel.writeStringUtf8("GET / HTTP/1.1${EOL}Host: youtrack.jetbrains.com${EOL}Connection: close${EOL}${EOL}")
        sendChannel.flush()
        println(socket.openReadChannel().readRemaining().readByteArray().toString(Charsets.UTF_8))
    }
}

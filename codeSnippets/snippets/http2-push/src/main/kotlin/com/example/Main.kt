package com.example

import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.*

fun main(args: Array<String>) {
    // generate SSL certificate
    val file = File("build/temporary.jks")
    if (!file.exists()) {
        file.parentFile.mkdirs()
        generateCertificate(file)
    }
    // run embedded server
    EmbeddedServer(CommandLineConfig(args).rootConfig, Netty).start()
}

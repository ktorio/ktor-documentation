package com.example

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.*
import java.io.*
import java.security.KeyStore

fun main() {
    embeddedServer(Netty, applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {
        envConfig()
    }, module = Application::module).start(wait = true)
}

private fun ApplicationEngine.Configuration.envConfig() {

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = "foobar"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    connector {
        port = 8080
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { "123456".toCharArray() },
        privateKeyPassword = { "foobar".toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}

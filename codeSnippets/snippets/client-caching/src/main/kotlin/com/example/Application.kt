package com.example

import cachingheaders.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.coroutines.*
import java.nio.file.*

fun main() {
    defaultServer(Application::module).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpCache) {
                val cacheFile = Files.createDirectories(Paths.get("build/cache")).toFile()
                publicStorage(FileStorage(cacheFile))
            }
            install(Logging) { level = LogLevel.INFO }
        }

        client.get("http://localhost:8080/index")
        client.get("http://localhost:8080/index")
        client.close()
    }
}

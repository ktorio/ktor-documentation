package com.example

import cachingheaders.*
import e2e.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.coroutines.*
import kotlinx.io.files.Path

fun main() {
    defaultServer(Application::module).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpCache) {
                publicStorage(FileStorage(Path("build/cache")))
            }
            install(Logging) { level = LogLevel.INFO }
        }

        client.get("http://localhost:8080/index")
        client.get("http://localhost:8080/index")
        client.close()
    }
}

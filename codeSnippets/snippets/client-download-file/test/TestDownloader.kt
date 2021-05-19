package io.ktor.samples.filedownload

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestDownloader {
    @Test
    fun downloadKtorMainPage(): Unit = runBlocking {
        val file = File.createTempFile("ktor", "test")
        HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.toString()) {
                        "https://ktor.io/index.html" -> respond("Ktor main page")
                        else -> error("Not expected request to ${request.url}")
                    }
                }
            }
        }.downloadMainPage(file)

        assertEquals("Ktor main page", file.readText())
    }
}
package io.ktor.samples.filedownload

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestDownloader {
    @Test
    fun downloadKtorMainPage(): Unit = runBlocking {
        val file = File.createTempFile("ktor", "test")
        val channel = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        when (request.url.toString()) {
                            "https://ktor.io/index.html" -> respond("Ktor main page")
                            else -> error("Not expected request to ${request.url}")
                        }
                    }
                }
            }.get<ByteReadChannel>("https://ktor.io/index.html") {
            onDownload { bytesSentTotal, contentLength ->
                println("Received $bytesSentTotal bytes from $contentLength")
            }
        }
        file.outputStream().use { fileStream ->
            channel.copyTo(fileStream)
        }

        assertEquals("Ktor main page", file.readText())
    }
}
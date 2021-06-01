package io.ktor.samples.filedownload

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    val client = HttpClient()
    val file = File.createTempFile("files", "index")

    runBlocking {
        client.downloadMainPage(file)
    }
}

suspend fun HttpClient.downloadMainPage(file: File) {
    val channel = get<ByteReadChannel>("https://ktor.io/index.html") {
        onDownload { bytesSentTotal, contentLength ->
            println("Received $bytesSentTotal bytes from $contentLength")
        }
    }

    file.outputStream().use { fileStream ->
        channel.copyTo(fileStream)
    }
}
package io.ktor.snippets.filedownload

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import java.io.File

fun main(args: Array<String>) {
    val client = HttpClient()

    runBlocking {
        val channel = client.get<ByteReadChannel>("https://ktor.io/index.html")
        val file = File.createTempFile("files", "index")

        file.outputStream().use {
            channel.copyTo(it)
        }
    }
}
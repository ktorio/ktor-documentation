package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.io.*
import kotlin.math.*

fun main() {
    runBlocking {
        val client = HttpClient()
        client.download("http://0.0.0.0:8080/download", File("ktor_logo.png"))
    }
}

suspend fun HttpClient.download(url: String, outFile: File, chunkSize: Int = 1024) {
    val length = head(url).headers[HttpHeaders.ContentLength]?.toLong() as Long
    val lastByte = length - 1

    var start = outFile.length()
    val output = FileOutputStream(outFile, true)

    while (true) {
        val end = min(start + chunkSize - 1, lastByte)
        val data = get(url) {
            header("Range", "bytes=${start}-${end}")
        }.body<ByteArray>()
        output.write(data)
        if (end >= lastByte) break
        start += chunkSize
    }
}

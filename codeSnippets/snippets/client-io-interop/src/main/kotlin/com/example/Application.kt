package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.RawSource
import kotlinx.io.buffered

fun main() {
    val client = HttpClient(CIO)
    runBlocking {
        client.prepareGet("https://httpbin.org/bytes/1024").execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.body()
            val source: RawSource = channel.asSource()
            val buffered = source.buffered()
            val firstByte = buffered.readByte()
            println("Read first byte from RawSource: $firstByte")
        }
    }
}

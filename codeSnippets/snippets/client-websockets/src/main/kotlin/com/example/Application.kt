package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.*

fun main() {
    val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/echo") {
            while(true) {
                val othersMessage = incoming.receive() as? Frame.Text
                println(othersMessage?.readText())
                val myMessage = Scanner(System.`in`).next()
                if(myMessage != null) {
                    send(myMessage)
                }
            }
        }
    }
    client.close()
}
package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test


class ApplicationTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClientDisconnectionCancelsRequest() = runTest {
        val server = embeddedServer(Netty, port = 8080) {
            module()
        }.start()

        val client = HttpClient(CIO)

        val job = launch {
            client.get("http://localhost:8080/long")
        }

        delay(300)
        job.cancelAndJoin() // Simulate client disconnect

        server.stop()
    }
}

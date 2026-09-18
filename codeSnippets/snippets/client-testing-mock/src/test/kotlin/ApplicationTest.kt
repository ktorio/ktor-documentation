package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiClientTest {
    @Test
    fun sampleClientTest() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = ApiClient(mockEngine)

            assertEquals("127.0.0.1", apiClient.getIp().ip)
        }
    }

    @Test
    fun mockMultipleEndpoints() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                when (request.url.encodedPath) {
                    "/user" -> respondOk("user-1")
                    "/orders" -> respondOk("order-1,order-2")
                    else -> error("Unhandled ${request.url}")
                }
            }
            val apiClient = ApiClient(mockEngine)

            assertEquals("user-1", apiClient.getUser())
            assertEquals("order-1,order-2", apiClient.getOrders())
        }
    }

    @Test
    fun mockCallChain() {
        runBlocking {
            val mockEngine = MockEngine.config {
                reuseHandlers = false
                addHandler { respondOk("step-1") }
                addHandler { respondOk("step-2") }
            }
            val client = HttpClient(mockEngine)

            assertEquals("step-1", client.get("https://api.example.com/a").body())
            assertEquals("step-2", client.get("https://api.example.com/b").body())
            assertFailsWith<IllegalStateException> {
                client.get("https://api.example.com/c").body<String>()
            }
        }
    }

    @Test
    fun mockCallChainWithQueue() {
        runBlocking {
            val engine = MockEngine.Queue()
            val client = HttpClient(engine)

            engine += { respondOk("first") }
            engine += { respondOk("second") }

            assertEquals("first", client.get("https://api.example.com/a").body())
            assertEquals("second", client.get("https://api.example.com/b").body())
        }
    }
}

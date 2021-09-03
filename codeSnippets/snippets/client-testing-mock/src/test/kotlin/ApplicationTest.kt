package com.example

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.junit.*
import kotlin.test.Test

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

            Assert.assertEquals("127.0.0.1", apiClient.getIp().ip)
        }
    }
}

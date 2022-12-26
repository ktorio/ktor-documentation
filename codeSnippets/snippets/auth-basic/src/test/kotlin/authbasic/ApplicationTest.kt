package authbasic

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testAuthRoute() = testApplication {
        val client = createClient {
            defaultRequest {
                val credentials = Base64.getEncoder().encodeToString("jetbrains:foobar".toByteArray())
                header(HttpHeaders.Authorization, "Basic $credentials")
            }
        }
        val response = client.get("/")
        assertEquals("Hello, jetbrains!", response.bodyAsText())
    }
}

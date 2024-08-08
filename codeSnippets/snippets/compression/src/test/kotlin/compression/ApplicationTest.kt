package compression

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/") {
            header(HttpHeaders.AcceptEncoding, "gzip, deflate")
        }
        assertEquals("deflate", response.headers[HttpHeaders.ContentEncoding])
    }
}

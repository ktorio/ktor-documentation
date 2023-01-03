import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import org.junit.*
import org.junit.Assert.*

class FreeMarkerTest {
    @Test
    fun test() = testApplication {
        val response: String = client.get("/index").body()
        assertEquals("""
            <html>
                <body>
                    <h1>Hello, John!</h1>
                </body>
            </html>
        """.trimIndent(),
            response
        )
    }
}
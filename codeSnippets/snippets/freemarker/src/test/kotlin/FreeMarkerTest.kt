import e2e.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class FreeMarkerTest: TestServer() {
    @Test
    fun test(): Unit = runBlocking {
        val response: String = HttpClient().get("http://localhost:8080/index").body()
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
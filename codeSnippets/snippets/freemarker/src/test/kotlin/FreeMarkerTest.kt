import e2e.TestServer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FreeMarkerTest: TestServer() {
    @Test
    fun test(): Unit = runBlocking {
        val response: String = HttpClient().get<HttpResponse>("http://localhost:8080/index").receive()
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
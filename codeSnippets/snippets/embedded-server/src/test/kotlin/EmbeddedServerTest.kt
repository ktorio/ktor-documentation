import e2e.TestServer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class EmbeddedServerTest: TestServer() {
    @Test
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {
        val response: String = HttpClient().get("http://localhost:8080/").body()
        assertEquals("Hello, world!", response)
    }
}
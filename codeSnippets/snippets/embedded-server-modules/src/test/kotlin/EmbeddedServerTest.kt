import e2e.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class EmbeddedServerTest: TestServer() {
    @Test
    fun rootRouteRespondsWithHelloString(): Unit = runBlocking {
        val response: String = HttpClient().get("http://localhost:8080/module").body()
        assertEquals("Hello from 'module'!", response)
    }
}
import e2e.runGradleApp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class EmbeddedServerTest {
    private lateinit var serverProcess: Process

    @Before
    fun runServer() {
        serverProcess = runGradleApp()
        serverProcess.waitFor(2, TimeUnit.SECONDS)
    }

    @After
    fun stopServer(): Unit = runBlocking {
        serverProcess.destroy()
    }

    @Test
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {
        val response: String = HttpClient().get<HttpResponse>("http://localhost:8000/").receive()
        assertEquals("Hello, world!", response)
    }
}
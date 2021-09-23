import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.snippets.plugin.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class CustomPluginTest {
    @Test
    fun testCustomHeader(): Unit = withTestApplication(Application::main) {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals("World", response.headers["Hello"])
        }
    }
}
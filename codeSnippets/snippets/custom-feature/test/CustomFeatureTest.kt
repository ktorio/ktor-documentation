import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.snippets.feature.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class CustomFeatureTest {
    @Test
    fun testCustomHeader(): Unit = withTestApplication(Application::main) {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals("World", response.headers["Hello"])
        }
    }
}
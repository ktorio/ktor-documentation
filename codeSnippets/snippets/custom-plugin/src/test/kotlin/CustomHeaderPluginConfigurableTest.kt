import com.example.plugins.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.*

class CustomHeaderPluginConfigurableTest {
    @Test
    fun testRoot() = testApplication {
        application {
            install(CustomHeaderPluginConfigurable)
        }
        client.get("/").apply {
            assertEquals("Some value", headers["X-Another-Custom-Header"].toString())
        }
    }
}

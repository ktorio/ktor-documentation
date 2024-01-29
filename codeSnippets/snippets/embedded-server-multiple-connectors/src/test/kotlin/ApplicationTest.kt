import com.example.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.testing.*
import org.junit.Test
import java.util.Properties
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testApi(): Unit = testApplication {
        testApplicationProperties {
            module { module() }
        }
        engine {
            envConfig()
        }
        client.get("/") {
            host = "0.0.0.0"
            port = 8080
        }.apply {
            assertEquals("Connected to public API", bodyAsText())
        }
        client.get("/") {
            host = "127.0.0.1"
            port = 9090
        }.apply {
            assertEquals("Connected to private API", bodyAsText())
        }
    }
}
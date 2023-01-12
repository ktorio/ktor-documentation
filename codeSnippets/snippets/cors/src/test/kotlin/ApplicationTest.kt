import com.example.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testFrontendPage(): Unit = testApplication {
        environment {
            envConfig()
        }
        client.get("/") {
            host = "0.0.0.0"
            port = 8081
        }.apply {
            assertEquals(
                """
<!DOCTYPE html>
<html>
  <head>
    <script src="/static/script.js"></script>
  </head>
  <body><button onclick="saveCustomer()">Make a CORS request to save a customer</button></body>
</html>

""".trimIndent(), bodyAsText())
        }
    }
}
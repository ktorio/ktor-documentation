import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testCssRoute() = testApplication {
        client.get("/styles.css").let {response ->
            assertEquals(
                """
                    body {
                    background-color: darkblue;
                    margin: 0;
                    }
                    h1.page-title {
                    color: white;
                    }
                    
            """.trimIndent(), response.bodyAsText())
        }
    }
}

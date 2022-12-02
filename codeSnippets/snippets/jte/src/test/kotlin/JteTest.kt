import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.*
import java.io.*
import kotlin.test.*
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/index")
        assertEquals("""
            <html>
                <body>
                    <h1>Hello, John!</h1>
                </body>
            </html>
        """.trimIndent(), response.bodyAsText())
    }

    @After
    fun deleteJteDir() {
        File("jte-classes").deleteRecursively()
    }
}

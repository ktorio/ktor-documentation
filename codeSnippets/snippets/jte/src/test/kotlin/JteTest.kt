import com.example.main
import com.example.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.*
import org.junit.Ignore
import java.io.*
import kotlin.test.*
import kotlin.test.Test

class ApplicationTest {
    @Ignore("jte-kotlin doesn't support Kotlin 2.3.0")
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/index")
        assertEquals(
            """
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

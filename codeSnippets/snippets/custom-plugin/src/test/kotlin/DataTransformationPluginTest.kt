import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class DataTransformationPluginTest {
    @Test
    fun testTransformData() = testApplication {
        val response = client.post("/transform-data") {
            setBody("10")
        }
        assertEquals("12", response.bodyAsText())
    }
}

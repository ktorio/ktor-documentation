import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun requestContainsCustomHeader(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("X-Custom-Header: Hello, world!"))
            assertTrue(outputString.contains("Content-Type: text/html"))
            assertTrue(outputString.contains("Read response delay (ms)"))
        }
    }
}
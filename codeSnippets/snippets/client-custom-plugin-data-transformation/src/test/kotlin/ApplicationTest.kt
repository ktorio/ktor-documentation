import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun responseContainsUserInfo(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("Userinfo: John;42"))
            assertTrue(outputString.contains("Username: Jane, age: 33"))
        }
    }
}
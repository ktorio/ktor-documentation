package slowserver

import io.ktor.client.request.*
import io.ktor.server.testing.*
import java.time.Duration
import java.time.LocalTime
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testPath1() = testApplication {
        val requestTime = LocalTime.now()
        client.get("/path1")
        val responseTime = LocalTime.now()
        assertTrue {
            Duration.between(requestTime, responseTime).toSeconds() >= 2
        }
    }
}

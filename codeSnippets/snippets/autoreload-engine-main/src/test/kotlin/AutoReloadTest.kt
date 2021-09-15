import com.example.main
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class AutoReloadTest {
    @Test
    fun developmentModeEnabledInHoconFile() {
        val configURL = Application::class.java.classLoader.getResource("application.conf")
        assertNotNull(configURL)
        val configFile = File(configURL!!.toURI())

        val config = ConfigFactory.parseFile(configFile)
        assertTrue(config.getBoolean("ktor.development"))
    }

    @Test
    fun serverRespondsByGetRequest() {
        withTestApplication {
            application.main()

            assertEquals("Hello, world!", handleRequest(HttpMethod.Get, "/").response.content)
        }
    }
}
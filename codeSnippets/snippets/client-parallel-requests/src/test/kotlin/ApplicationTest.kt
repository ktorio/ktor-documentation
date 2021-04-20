import e2e.readString
import e2e.runGradleApp
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.slf4j.helpers.NOPLogger

class ApplicationTest {
    @Test
    fun concatenatedResponseFromBothRequests(): Unit = runBlocking {
        val server = embeddedServer(CIO, environment = applicationEngineEnvironment {
            log = NOPLogger.NOP_LOGGER

            connector {
                port = 8080
            }
            module {
                routing {
                    get("/path1") { call.respondText { "path1" } }
                    get("/path2") { call.respondText { "path2" } }
                }
            }
        })

        server.start(wait = false)

        val output = runGradleApp().inputStream.readString()
        assertEquals("path1\npath2\n", output)

        server.stop(100, 100)
    }
}
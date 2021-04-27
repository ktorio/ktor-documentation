import e2e.WithTestServer
import e2e.defaultServer
import e2e.readString
import e2e.runGradleAppWaiting
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest: WithTestServer() {
    override val server = defaultServer {
        routing {
            get("/path1") { call.respondText { "path1" } }
            get("/path2") { call.respondText { "path2" } }
        }
    }

    @Test
    fun concatenatedResponseFromBothRequests(): Unit = runBlocking {
        assertEquals("path1\npath2\n", runGradleAppWaiting().inputStream.readString())
    }
}
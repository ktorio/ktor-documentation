package e2e

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.junit.After
import org.junit.Before

abstract class WithTestServer {
    protected abstract val server: EmbeddedServer<CIOApplicationEngine,CIOApplicationEngine.Configuration>

    @Before
    fun startServer() {
        server.start(wait = false)
    }

    @After
    fun stopServer() {
        server.stop(100, 100)
    }
}
package e2e

import io.ktor.server.engine.*
import org.junit.After
import org.junit.Before

abstract class WithTestServer {
    protected abstract val server: ApplicationEngine

    @Before
    fun startServer() {
        server.start(wait = false)
    }

    @After
    fun stopServer() {
        server.stop(100, 100)
    }
}
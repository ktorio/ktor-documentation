package e2e

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit

abstract class TestServer {
    private lateinit var serverProcess: Process

    @Before
    fun runServer() {
        serverProcess = runGradleApp()
        serverProcess.waitFor(4, TimeUnit.SECONDS)
    }

    @After
    fun stopServer(): Unit = runBlocking {
        serverProcess.destroy()
    }
}
import e2e.readString
import e2e.runGradleWaiting
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.Assert.assertEquals
import java.net.ServerSocket
import java.util.concurrent.TimeUnit

class DockerTest {
    private lateinit var dockerRunProcess: Process
    private var port: Int = -1

    @Before
    fun runDocker() {
        runGradleWaiting("installDist")
        checkDockerBinary()
        val dockerBuild = runDocker("build", "-t", "docker-project", ".")
        dockerBuild.waitFor()
        assertProcessExited(dockerBuild)

        port = getFreePort()
        dockerRunProcess = runDocker("run", "-p", "$port:8080", "docker-project")
        dockerRunProcess.waitFor(4, TimeUnit.SECONDS)
    }

    @After
    fun killContainer() {
        dockerRunProcess.destroy()
    }

    @Test
    @Ignore
    fun responseFromServerMatchesRegex() {
        val response: String = runBlocking { HttpClient().get("http://localhost:$port/").body() }

        assertThat(
            response,
            Matchers.matchesPattern(
"""
<!DOCTYPE html>
<html>
  <head>
    <title>Ktor: Docker</title>
  </head>
  <body>
    <p>Hello from Ktor Netty engine running in Docker sample application</p>
    <p>Runtime.getRuntime\(\).availableProcessors\(\): \d*</p>
    <p>Runtime.getRuntime\(\).freeMemory\(\): \d+</p>
    <p>Runtime.getRuntime\(\).totalMemory\(\): \d+</p>
    <p>Runtime.getRuntime\(\).maxMemory\(\): \d+</p>
    <p>System.getProperty\(&quot;user.name&quot;\): \w+</p>
  </body>
</html>

""".trimIndent()
            )
        )
    }

    private fun checkDockerBinary() {
        val process = runDocker("--version")
        process.waitFor()
        assertProcessExited(process)
    }

    private fun runDocker(vararg arg: String): Process {
        return ProcessBuilder(listOf("/usr/bin/env", "docker") + arg).start()
    }

    private fun assertProcessExited(process: Process, code: Int = 0) {
        assertEquals(process.errorStream.readString(), code, process.exitValue())
    }

    private fun getFreePort(): Int {
        val socket = ServerSocket(0)
        val port = socket.localPort
        socket.close()
        return port
    }
}
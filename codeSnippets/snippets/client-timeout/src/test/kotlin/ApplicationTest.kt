import e2e.WithTestServer
import e2e.defaultServer
import e2e.readString
import e2e.runGradleAppWaiting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.matchesPattern
import org.junit.Test

class ApplicationTest: WithTestServer() {
    override val server = defaultServer {
        routing {
            get("/path1") { call.respondText { "result" } }
        }
    }

    @Test
    fun applicationOutputMatchesPattern() {
        assertThat(
            runGradleAppWaiting().inputStream.readString(),
            matchesPattern(
                """^Request time: \d{2}:\d{2}:\d{2}\.\d+
                |result
                |""".trimMargin()
            )
        )
    }
}
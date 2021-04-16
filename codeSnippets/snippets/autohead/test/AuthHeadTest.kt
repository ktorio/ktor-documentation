import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.snippets.autohead.*
import org.junit.Test
import kotlin.test.assertEquals

class AuthHeadTest {

    @Test
    fun respondsSuccessfullyForHeadRequest() {
        withTestApplication {
            application.main()

            assertEquals(HttpStatusCode.OK, handleRequest(HttpMethod.Head, "/home").response.status())

            assertEquals(
                "This is a response to a GET, but HEAD also works",
                handleRequest(HttpMethod.Get, "/home").response.content
            )
        }
    }
}
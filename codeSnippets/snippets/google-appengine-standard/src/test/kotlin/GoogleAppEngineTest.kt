package io.ktor.snippets.hello

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class GoogleAppEngineTest {
    @Test
    fun test() {
        withTestApplication {
            application.main()

            assertEquals(
                """
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <title>Ktor: google-appengine-standard</title>
                      </head>
                      <body>
                        <p>Hello from Ktor Google Appengine Standard sample application</p>
                      </body>
                    </html>

                """.trimIndent(),
                handleRequest(HttpMethod.Get, "/").response.content
            )
        }
    }
}
package com.example

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class HtmlTest {
    @Test
    fun test() {
        withTestApplication {
            application.module()

            assertEquals(
                """
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <title>Ktor</title>
                      </head>
                      <body>
                        <h1>Hello from Ktor!</h1>
                      </body>
                    </html>
                    
                """.trimIndent(),
                handleRequest(HttpMethod.Get, "/").response.content
            )
        }
    }
}
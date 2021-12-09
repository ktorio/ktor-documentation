package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class HtmlTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
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
                    
            """.trimIndent(), response.bodyAsText())
    }
}

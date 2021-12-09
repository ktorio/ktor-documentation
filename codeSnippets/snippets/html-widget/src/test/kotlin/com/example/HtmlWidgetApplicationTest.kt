package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class HtmlWidgetApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """
                    |<!DOCTYPE html>
                    |<html>
                    |  <head>
                    |    <title>Ktor: html</title>
                    |  </head>
                    |  <body>
                    |    <p>Hello from Ktor html sample application</p>
                    |    <div>Widgets are just functions</div>
                    |  </body>
                    |</html>
                    |
                """.trimMargin(),
            response.bodyAsText()
        )
    }
}

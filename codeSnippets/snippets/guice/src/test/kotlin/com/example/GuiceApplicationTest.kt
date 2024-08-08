package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class GuiceApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            main()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """
                    |<!DOCTYPE html>
                    |<html>
                    |  <head>
                    |    <title>Ktor: guice</title>
                    |  </head>
                    |  <body>
                    |    <p>Hello from Ktor Guice sample application</p>
                    |    <p>Call Information: /</p>
                    |  </body>
                    |</html>
                    |
                """.trimMargin(),
            response.bodyAsText()
        )
    }
}

package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.*

class HtmlTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(
            """
<!DOCTYPE html>
<html>
  <body>
    <h1>Ktor</h1>
    <article>
      <h2>Hello from Ktor!</h2>
      <p>Kotlin Framework for creating connected systems.</p>
      <ul>
        <li><b>One</b></li>
        <li>Two</li>
      </ul>
    </article>
  </body>
</html>

            """.trimIndent(), response.bodyAsText())
    }
}

package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testFormAuth() = testApplication {
        val loginResponse = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("username" to "jetbrains", "password" to "foobar").formUrlEncode())
        }
        assertEquals("Hello, jetbrains!", loginResponse.bodyAsText())
    }

    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(
            """
<!DOCTYPE html>
<html>
  <body>
    <form action="/login" enctype="application/x-www-form-urlencoded" method="post">
      <p>Username:<input type="text" name="username"></p>
      <p>Password:<input type="password" name="password"></p>
      <p><input type="submit" value="Login"></p>
    </form>
  </body>
</html>

            """.trimIndent(), response.bodyAsText())
    }
}

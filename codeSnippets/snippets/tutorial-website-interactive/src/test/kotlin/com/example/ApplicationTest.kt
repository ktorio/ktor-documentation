package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <title>Kotlin Journal</title>
                        </head>
                        <body style="text-align: center; font-family: sans-serif">
                        <img src="/static/ktor_logo.png">
                        <h1>Kotlin Ktor Journal </h1>
                        <p><i>Powered by Ktor, kotlinx.html & Freemarker!</i></p>
                        <hr>
                            <div>
                                <h3>The drive to develop!</h3>
                                <p>...it&#39;s what keeps me going.</p>
                            </div>
                        <hr>
                        <div>
                            <h3>Add a new journal entry!</h3>
                            <form action="/submit" method="post">
                                <input type="text" name="headline">
                                <br>
                                <textarea name="body"></textarea>
                                <br>
                                <input type="submit">
                            </form>
                        </div>
                        </body>
                        </html>
                    """.trimIndent(),
            response.bodyAsText()
        )
    }
}

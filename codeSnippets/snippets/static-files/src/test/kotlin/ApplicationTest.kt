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
        assertEquals(
            """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Static page</title>
    <link rel="stylesheet" href="assets/styles.css">
    <script src="assets/script.js"></script>
</head>
<body>
<img src="images/ktor_logo.png" alt="ktor logo">
<h1>Static page</h1>
<p>Welcome to my static page!</p>
<button type="button" onclick="message('Hello, world!')">Click Me!</button>
</body>
</html>
            """.trimIndent(),
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

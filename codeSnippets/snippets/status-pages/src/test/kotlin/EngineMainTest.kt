package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testInternalError() = testApplication {
        application {
            module()
        }
        val response = client.get("/internal-error")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("500: java.lang.Exception: Internal Server Error", response.bodyAsText())
    }

    @Test
    fun testMissingPage() = testApplication {
        application {
            module()
        }
        val response = client.get("/missing-page")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("404: Page Not Found", response.bodyAsText())
    }

    @Test
    fun testPaymentRequired() = testApplication {
        application {
            module()
        }
        val response = client.get("/payment-error")
        assertEquals(HttpStatusCode.PaymentRequired, response.status)
        assertEquals(
            """
<!doctype html>
<html lang="en">
<head>
    <title>Status pages</title>
</head>
<body>
<h1>402: Payment Required Error</h1>
</body>
</html>
                """.trimMargin(),
            response.bodyAsText()
        )
    }
}

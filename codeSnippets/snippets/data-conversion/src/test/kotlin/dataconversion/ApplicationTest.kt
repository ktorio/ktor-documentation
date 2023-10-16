package dataconversion

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testArticleEnum() = testApplication {
        val response = client.get("/?article=a")
        assertEquals("The received article is A", response.bodyAsText())
    }

    @Test
    fun testConvertedDate() = testApplication {
        val response = client.get("/date?date=20170501")
        assertEquals("The date is 2017-05-01", response.bodyAsText())
    }
}

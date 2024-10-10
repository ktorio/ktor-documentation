package dataconversion

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testConvertedDate() = testApplication {
        application {
            module()
        }
        val response = client.get("/date?date=20170501")
        assertEquals("The date is 2017-05-01", response.bodyAsText())
    }
}

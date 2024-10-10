package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class GsonAppTest {
    @Ignore
    @Test
    fun testV1() = testApplication {
        application {
            main()
        }
        val response = client.get("/v1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """
                    |{
                    |  "name": "root",
                    |  "items": [
                    |    {
                    |      "key": "A",
                    |      "value": "Apache"
                    |    },
                    |    {
                    |      "key": "B",
                    |      "value": "Bing"
                    |    }
                    |  ],
                    |  "date": {
                    |    "year": 2018,
                    |    "month": 4,
                    |    "day": 13
                    |  }
                    |}
                """.trimMargin(),
            response.bodyAsText()
        )
    }

    @Ignore
    @Test
    fun testV1ItemKey() = testApplication {
        val response = client.get("/v1/item/B")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """
                    |{
                    |  "key": "B",
                    |  "value": "Bing"
                    |}
                """.trimMargin(),
            response.bodyAsText()
        )
    }
}

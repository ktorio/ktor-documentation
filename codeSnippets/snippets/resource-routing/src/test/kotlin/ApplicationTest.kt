package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testGetAllArticles() = testApplication {
        val response = client.get("/articles?sort=old")
        assertEquals("List of articles sorted starting from old", response.bodyAsText())
    }

    @Test
    fun testGetArticle() = testApplication {
        val response = client.get("/articles/12")
        assertEquals("An article with id 12", response.bodyAsText())
    }

    @Test
    fun testPostArticle() = testApplication {
        val response = client.post("/articles")
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("An article is saved", response.bodyAsText())
    }
}

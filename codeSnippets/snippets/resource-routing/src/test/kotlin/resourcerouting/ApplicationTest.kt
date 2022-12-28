package resourcerouting

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.resources.*
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

    @Test
    fun testHrefGenerationFromResource() = testApplication {
        application {
            assertEquals("/articles?sort=new", href(Articles()))
            assertEquals("/articles?sort=old", href(Articles(sort = "old")))
            assertEquals("/articles", href(Articles(sort = null)))
            assertEquals("/articles/new?sort=new", href(Articles.New()))
            assertEquals("/articles/1?sort=new", href(Articles.Id(id = 1)))
            assertEquals("/articles/1", href(Articles.Id(Articles(sort = null), id = 1)))
            assertEquals("/articles/123/edit?sort=new", href(Articles.Id.Edit(Articles.Id(id = 123))))

            val urlBuilder1 = URLBuilder(URLProtocol.HTTPS, "ktor.io")
            href(Articles(sort = null), urlBuilder1)
            assertEquals("https://ktor.io/articles", urlBuilder1.buildString())

            val urlBuilder2 = URLBuilder(host = "ktor.io", parameters = parametersOf("token", "123"))
            href(Articles(sort = null), urlBuilder2)
            assertEquals("http://ktor.io/articles?token=123", urlBuilder2.buildString())
        }
    }
}

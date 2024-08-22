package jsonkotlinx

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.test.*

class CustomerTests {
    @Test
    fun testGetCustomer() = testApplication {
        application {
            main()
        }
        val response = client.get("/customer/1")
        assertEquals(
            """
                {
                    "id": 1,
                    "firstName": "Jane",
                    "lastName": "Smith"
                }
            """.trimIndent(),
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostCustomer() = testApplication {
        application {
            main()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(Customer(3, "Jet", "Brains"))
        }
        assertEquals("Customer stored correctly", response.bodyAsText())
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testPostCustomerLegacyApi() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Post, "/customer"){
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Json.encodeToString(Customer(3, "Jet", "Brains")))
        }) {
            assertEquals("Customer stored correctly", response.content)
            assertEquals(HttpStatusCode.Created, response.status())
        }
    }
}

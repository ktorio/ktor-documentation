package com.example

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {
    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        configure()

        val response = client.get("/tasks/byPriority/Medium")
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(body, "Mow the lawn")
        assertContains(body, "Paint the fence")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        configure()

        val response = client.get("/tasks/byPriority/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        configure()

        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun newTasksCanBeAdded() = testApplication {
        configure()

        val response1 = client.post("/tasks") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.FormUrlEncoded.toString()
            )
            setBody(
                listOf(
                    "name" to "swimming",
                    "description" to "Go to the beach",
                    "priority" to "Low"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.NoContent, response1.status)

        val response2 = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response2.status)
        val body = response2.bodyAsText()

        assertContains(body, "swimming")
        assertContains(body, "Go to the beach")
    }
}
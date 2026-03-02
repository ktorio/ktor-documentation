package com.example

import com.example.model.Priority
import com.example.model.Task
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.deserialize
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
            configureSerialization()
            configureSockets()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(WebSockets.Plugin) {
                contentConverter =
                    KotlinxWebsocketSerializationConverter(Json.Default)
            }
        }

        val expectedTasks = listOf(
            Task("cleaning", "Clean the house", Priority.Low),
            Task("gardening", "Mow the lawn", Priority.Medium),
            Task("shopping", "Buy the groceries", Priority.High),
            Task("painting", "Paint the fence", Priority.Medium)
        )
        var actualTasks = emptyList<Task>()

        client.webSocket("/tasks") {
            consumeTasksAsFlow().collect { allTasks ->
                actualTasks = allTasks
            }
        }

        assertEquals(expectedTasks.size, actualTasks.size)
        expectedTasks.forEachIndexed { index, task ->
            assertEquals(task, actualTasks[index])
        }
    }

    private fun DefaultClientWebSocketSession.consumeTasksAsFlow() = incoming
        .consumeAsFlow()
        .map {
            converter!!.deserialize<Task>(it)
        }
        .scan(emptyList<Task>()) { list, task ->
            list + task
        }
}
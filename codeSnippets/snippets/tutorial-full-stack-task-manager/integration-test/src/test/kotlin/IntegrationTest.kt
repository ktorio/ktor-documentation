package org.example.ktor

import Priority
import Task
import TaskApi
import core.extension.createConfiguredClient
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertTrue


class IntegrationTest {

    /**
     * Tests adding a new Task and verifies that it has been added successfully.
     */
    @Test
    fun testAddNewTask() = testApplication {
        val client = createConfiguredClient()

        val taskApi = TaskApi(client)
        application { module() }

        val newTask = Task("TestTask", "A new task for testing", Priority.High)
        taskApi.updateTask(newTask)
        val tasks = taskApi.getAllTasks()
        assertTrue { tasks.contains(newTask) }
    }

    /**
     * Tests deleting a Task and verifies that it has been removed successfully.
     */
    @Test
    fun testDeleteTask() = testApplication {
        val client = createConfiguredClient()

        val taskApi = TaskApi(client)
        application { module() }

        val newTask = Task("DeleteTask", "Task to be deleted", Priority.Medium)
        taskApi.updateTask(newTask)
        taskApi.removeTask(newTask)
        val remainingTasks = taskApi.getAllTasks()
        assertTrue { !remainingTasks.contains(newTask) }
    }
}

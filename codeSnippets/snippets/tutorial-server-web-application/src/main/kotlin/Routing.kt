package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.server.http.content.*
import io.ktor.server.request.receiveParameters

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/html-thymeleaf") {
            call.respond(ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))))
        }
        staticResources("/static", "static")

        route("/tasks") {
            get {
                val tasks = TaskRepository.allTasks()
                call.respond(
                    ThymeleafContent("all-tasks", mapOf("tasks" to tasks))
                )
            }
            get("/byName") {
                val name = call.request.queryParameters["name"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(
                    ThymeleafContent("single-task", mapOf("task" to task))
                )
            }
            get("/byPriority") {
                val priorityAsText = call.request.queryParameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.tasksByPriority(priority)


                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    val data = mapOf(
                        "priority" to priority,
                        "tasks" to tasks
                    )
                    call.respond(ThymeleafContent("tasks-by-priority", data))
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post {
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )
                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val priority = Priority.valueOf(params.third)
                    TaskRepository.addTask(
                        Task(
                            params.first,
                            params.second,
                            priority
                        )
                    )
                    val tasks = TaskRepository.allTasks()
                    call.respond(
                        ThymeleafContent("all-tasks", mapOf("tasks" to tasks))
                    )
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
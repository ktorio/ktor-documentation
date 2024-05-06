package com.example.model

import Priority
import Task

interface TaskRepository {
    suspend fun allTasks(): List<Task>
    suspend fun tasksByPriority(priority: Priority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun addTask(task: Task)
    suspend fun removeTask(name: String): Boolean
}
package com.example.model

import Priority
import Task

interface TaskRepository {
    fun allTasks(): List<Task>
    fun tasksByPriority(priority: Priority): List<Task>
    fun taskByName(name: String): Task?
    fun addTask(task: Task)
    fun removeTask(name: String): Boolean
}
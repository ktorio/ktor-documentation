package com.example.ktor.full_stack_task_manager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
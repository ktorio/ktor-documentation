package com.example.ktor.full_stack_task_manager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "full_stack_task_manager",
    ) {
        App()
    }
}
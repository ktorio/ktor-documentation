package com.example.ktor.full_stack_task_manager

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    val state = WindowState(
        size = DpSize(400.dp, 600.dp),
        position = WindowPosition(200.dp, 100.dp)
    )
    Window(
        title = "Task Manager (Desktop)",
        state = state,
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
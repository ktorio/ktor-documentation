package com.example.ktor.full_stack_task_manager

import Task
import TaskApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ktor.full_stack_task_manager.network.createHttpClient
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        val httpClient = createHttpClient()
        val taskApi = remember { TaskApi(httpClient) }
        val tasks = remember { mutableStateOf(emptyList<Task>()) }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                scope.launch {
                    tasks.value = taskApi.getAllTasks()
                }
            }) {
                Text("Fetch Tasks")
            }
            for (task in tasks.value) {
                Text(task.name)
            }
        }
    }
}
package com.example.digest

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun containsAuthGreeting(): Unit = runBlocking {
        assertTrue(runGradleAppWaiting().inputStream.readString().contains("Hello, jetbrains!"))
    }
}
package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun outputContainsCustomServerError(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("Custom server error: http://localhost:8080/error. Status: 500 Internal Server Error. Text: \"Code: 3, message: Some custom error\""))
        }
    }
}

package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun outputContainsAllResponses(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("RESPONSE: 500 Internal Server Error"))
            assertTrue(outputString.contains("RESPONSE: 200 OK"))
            assertTrue(outputString.contains("Server is back online!"))
        }
    }
}
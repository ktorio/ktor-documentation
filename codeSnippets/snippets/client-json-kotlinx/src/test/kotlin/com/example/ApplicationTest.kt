package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun outputContainsResponseData(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("Customer stored correctly"))
            assertTrue(outputString.contains("First name: 'Jet', last name: 'Brains'"))
        }
    }
}
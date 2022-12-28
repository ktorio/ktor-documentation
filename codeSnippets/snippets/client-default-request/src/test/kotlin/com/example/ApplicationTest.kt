package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun requestContainsCustomHeader(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("200 OK"))
        }
    }
}
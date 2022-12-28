package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*

class ApplicationTest {
    @Test
    fun applicationOutputMatchesPattern(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            val requestTimeSubstring = "Request time: \\d{2}:\\d{2}:\\d{2}\\.\\d+".toRegex()
            Assert.assertTrue(outputString.contains(requestTimeSubstring))
        }
    }
}

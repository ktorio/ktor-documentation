package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun outputContainsMissingPageException(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("Missing page: https://ktor.io/docs/missing-page.html. Status: 404 Not Found."))
        }
    }
}

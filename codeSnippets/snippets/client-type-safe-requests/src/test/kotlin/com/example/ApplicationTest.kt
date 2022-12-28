package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*

class ApplicationTest {
    @Test
    fun outputContainsServerResponses(): Unit = runBlocking {
        runGradleAppWaiting().inputStream.readString().let { outputString ->
            assertTrue(outputString.contains("List of articles sorted starting from new"))
            assertTrue(outputString.contains("Create a new article"))
            assertTrue(outputString.contains("An article is saved"))
            assertTrue(outputString.contains("An article with id 12"))
            assertTrue(outputString.contains("Edit an article with id 12"))
            assertTrue(outputString.contains("An article with id 12 updated"))
            assertTrue(outputString.contains("An article with id 12 deleted"))
        }
    }
}

package com.example

import e2e.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*
import java.io.*

class ApplicationTest {
    @Before
    fun deleteCacheFile() {
        val files = File("build/cache").listFiles()
        files?.forEach { file ->
            file.delete()
        }
    }

    @Test
    fun containsRequestLog(): Unit = runBlocking {
        File("build/cache").delete()
        assertTrue(runGradleAppWaiting().inputStream.readString().contains("REQUEST: http://localhost:8080/index"))
    }
}
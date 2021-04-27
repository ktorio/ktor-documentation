package com.example

import e2e.readString
import e2e.runGradleAppWaiting
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ApplicationTest {
    @Test
    fun testLoggerOutputInStdout() {
        val output = runGradleAppWaiting().inputStream.readString()

        assertThat(output, containsString("REQUEST: https://ktor.io/"))
        assertThat(output, containsString("METHOD: HttpMethod(value=GET)"))
        assertThat(output, containsString("CONTENT HEADERS"))
        assertThat(output, containsString("COMMON HEADERS"))
    }
}
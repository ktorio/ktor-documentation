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

        assertThat(output, containsString("REQUEST: https://ktor.io/docs/welcome.html"))
        assertThat(output, containsString("REQUEST: https://ktor.io?token=abc123"))
        assertThat(output, containsString("REQUEST: https://ktor.io#some_anchor"))
        assertThat(output, containsString("REQUEST: https://ktor.io"))
    }
}
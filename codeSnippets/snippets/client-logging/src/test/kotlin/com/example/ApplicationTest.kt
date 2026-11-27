package com.example

import e2e.readString
import e2e.runGradleWaiting
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.Assert.assertEquals

class ApplicationTest {
    @Test
    fun testLoggerOutputInStdout() {
        val process = runGradleWaiting("run", "--args=--development")
        val output = process.inputStream.readString()
        assertEquals(0, process.exitValue())

        assertThat(output, containsString("REQUEST: https://ktor.io/"))
        assertThat(output, containsString("METHOD: GET"))
        assertThat(output, containsString("CONTENT HEADERS"))
        assertThat(output, containsString("COMMON HEADERS"))
    }

    @Test
    fun testHttpLoggingDisabledWithoutDevelopmentFlag() {
        val process = runGradleWaiting("run")
        val output = process.inputStream.readString()
        assertEquals(0, process.exitValue())
        assertThat(output, not(containsString("REQUEST:")))
        assertThat(output, not(containsString("RESPONSE:")))
    }
}

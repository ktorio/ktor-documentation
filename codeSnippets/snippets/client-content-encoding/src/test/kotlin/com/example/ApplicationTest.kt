package com.example

import e2e.readString
import e2e.runGradleAppWaiting
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ApplicationTest {
    @Test
    fun outputContainsCompressionHeader() {
        val output = runGradleAppWaiting().inputStream.readString()

        assertThat(output, containsString("Content-Encoding: deflate"))
        assertThat(output, containsString("Body: Hello, world!"))
    }
}
package com.example

import e2e.readString
import e2e.runGradleAppWaiting
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ApplicationTest {
    @Test
    fun outputContainsSessionData() {
        val output = runGradleAppWaiting().inputStream.readString()

        assertThat(output, containsString("Session ID is 123abc. Reload count is 1."))
        assertThat(output, containsString("Session ID is 123abc. Reload count is 2."))
        assertThat(output, containsString("Session ID is 123abc. Reload count is 3."))
    }
}
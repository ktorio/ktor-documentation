package com.example

import org.junit.Test
import io.ktor.server.testing.*
import io.ktor.http.*
import org.junit.Assert.*

class DownloadFileTest {
    @Test
    fun respondWithPNGFileAndContentDispositionHeader() {
        withTestApplication {
            application.main()

            val response = handleRequest(HttpMethod.Get, "/download").response
            val content = response.byteContent ?: ByteArray(4)

            assertPNG(content)
            assertEquals(
                    "attachment; filename=ktor_logo.png",
                    response.headers[HttpHeaders.ContentDisposition]
            )
        }
    }

    private fun assertPNG(array: ByteArray) {
        assertEquals((0x89).toByte(), array[0])
        assertEquals((0x50).toByte(), array[1])
        assertEquals((0x4E).toByte(), array[2])
        assertEquals((0x47).toByte(), array[3])
    }


}

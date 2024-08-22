package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.*
import org.junit.Assert.*

class DownloadFileTest {
    @Test
    fun respondWithPNGFileAndContentDispositionHeader() = testApplication {
        application {
            main()
        }
        val response = client.get("/download")
        val content = response.readBytes(4)
        assertPNG(content)
        assertEquals(
            "attachment; filename=ktor_logo.png",
            response.headers[HttpHeaders.ContentDisposition]
        )
    }

    @Test
    fun respondWithTxtFileFromPath() = testApplication {
        application {
            main()
        }
        val response = client.get("/downloadFromPath")
        assertEquals("Just a simple text file.", response.bodyAsText().trim())
    }

    private fun assertPNG(array: ByteArray) {
        assertEquals((0x89).toByte(), array[0])
        assertEquals((0x50).toByte(), array[1])
        assertEquals((0x4E).toByte(), array[2])
        assertEquals((0x47).toByte(), array[3])
    }
}

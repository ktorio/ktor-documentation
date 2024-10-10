package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.After
import java.io.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testPost() = testApplication {
        application {
            main()
        }
        val response = client.post("/text") {
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("Hello, world!")
        }
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testPostByteArray() = testApplication {
        application {
            main()
        }
        val response = client.post("/bytes") {
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("Hello, world!")
        }
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testPostChannel() = testApplication {
        application {
            main()
        }
        val response = client.post("/channel") {
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("Hello, world!")
        }
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testUploadBinary() = testApplication {
        application {
            main()
        }
        val response = client.post("/upload") {
            setBody(File("ktor_logo.png").readBytes())
        }
        assertEquals("A file is uploaded", response.bodyAsText())
    }

    @After
    fun deleteUploadedFile() {
        File("uploads/ktor_logo.png").delete()
    }
}

package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.*

fun Application.module() {
    routing {
        /*
        staticFiles("/", File("files"))
        */
        staticFiles("/", File("files")) {
            default("index.html")
            preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
            modify { file, call ->
                call.response.headers.append(HttpHeaders.ETag, file.name.toString())
            }
        }
        staticFiles("/files", File("textFiles")) {
            default("html-file.txt")
            exclude { file -> file.path.contains("excluded") }
            contentType { file ->
                when (file.name) {
                    "html-file.txt" -> ContentType.Text.Html
                    else -> null
                }
            }
            cacheControl { file ->
                when (file.name) {
                    "file.txt" -> listOf(Immutable, CacheControl.MaxAge(10000))
                    else -> emptyList()
                }
            }
        }
    }
}

object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}
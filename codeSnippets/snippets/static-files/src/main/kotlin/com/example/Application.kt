package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.*
import io.ktor.util.date.GMTDate
import java.io.*

fun Application.module() {
    routing {
        staticFiles("/resources", File("files"))
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
            fallback { requestedPath, call ->
                when {
                    requestedPath.endsWith(".php") -> call.respondRedirect("/static/index.html") // absolute path
                    requestedPath.endsWith(".kt") -> call.respondRedirect("Default.kt") // relative path
                    requestedPath.endsWith(".xml") -> call.respond(HttpStatusCode.Gone)
                    else -> call.respondFile(File("files/index.html"))
                }
            }
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

        staticFiles("/static", filesDir) {
            etag { resource -> EntityTagVersion("etag") }
            lastModified { resource -> GMTDate() }
        }

        staticFiles("/static", filesDir) {
            etag(ETagProvider.StrongSha256)
        }
    }
}

object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}
package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.nio.file.Paths

fun Application.module() {
    routing {
        /*
        staticZip("/", "", Paths.get("files/text-files.zip"))
        */
        staticZip("/", "", Paths.get("files/text-files.zip")) {
            default("file.txt")
            modify { path, call ->
                call.response.headers.append(HttpHeaders.ETag, path.fileName.toString())
            }
        }
    }
}

object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}
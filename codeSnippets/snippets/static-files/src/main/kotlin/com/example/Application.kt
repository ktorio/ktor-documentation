package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.*

fun Application.module() {
    routing {
        staticFiles("/", File("files")) {
            default("index.html")
            preCompressed(CompressedFileType.GZIP)
        }
    }
}

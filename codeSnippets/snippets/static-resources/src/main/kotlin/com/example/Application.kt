package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File


fun Application.module() {
    routing {
        staticResources("/", "static") {
            default("index.html")
            preCompressed(CompressedFileType.GZIP)
        }
    }
}

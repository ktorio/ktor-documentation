package com.example

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.module() {
    routing {
        /*
        staticResources("/", "static")
        staticResources("/", "static", index = "custom_index.html")
         */
        staticResources("/", "static"){
            extensions("html", "htm")
            enableAutoHeadResponse()
            preCompressed(CompressedFileType.GZIP)
            exclude { url -> url.path.contains("excluded") }
        }
    }
}
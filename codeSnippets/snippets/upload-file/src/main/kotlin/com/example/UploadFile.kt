package com.example

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.main() {
    routing {
        var fileDescription = ""
        var fileName = ""

        post("/upload") {
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        var fileBytes = part.streamProvider().readBytes()
                        File("uploads/$fileName").writeBytes(fileBytes)
                    }
                }
            }

            call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
        }
    }
}

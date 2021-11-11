package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.html.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing  {
        get("/"){
            call.respondHtml {
                head {
                    title { +"Ktor: post" }
                }
                body {
                    p {
                        +"Hello from Ktor Post sample application"
                    }
                    p {
                        +"File upload"
                    }
                    form("/form", encType = FormEncType.multipartFormData, method = FormMethod.post) {
                        acceptCharset = "utf-8"
                        p {
                            label { +"Text field: " }
                            textInput { name = "textField" }
                        }
                        p {
                            label { +"File field: " }
                            fileInput { name = "fileField" }
                        }
                        p {
                            submitInput { value = "send" }
                        }
                    }
                }
            }
        }

        post("/form") {
            val multipart = call.receiveMultipart()
            call.respondTextWriter {
                if (!call.request.isMultipart()) {
                    appendLine("Not a multipart request")
                } else {
                    while (true) {
                        val part = multipart.readPart() ?: break
                        when (part) {
                            is PartData.FormItem ->
                                appendLine("FormItem: ${part.name} = ${part.value}")
                            is PartData.FileItem ->
                                appendLine("FileItem: ${part.name} -> ${part.originalFileName} of ${part.contentType}")
                        }
                        part.dispose()
                    }
                }
            }
        }
    }
}

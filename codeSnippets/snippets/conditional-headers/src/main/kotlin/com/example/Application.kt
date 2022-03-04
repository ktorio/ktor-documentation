package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.html.*
import java.io.*
import java.util.*

fun Application.main() {
    install(ConditionalHeaders) {
        val file = File("src/main/kotlin/com/example/Application.kt")
        version { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> listOf(
                    EntityTagVersion(file.lastModified().hashCode().toString()),
                    LastModifiedVersion(Date(file.lastModified()))
                )
                else -> emptyList()
            }
        }
    }
    routing {
        get("/html-dsl") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1(classes = "page-title") {
                        +"Hello from Ktor!"
                    }
                }
            }
        }
        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.blue
                    margin(0.px)
                }
                rule("h1.page-title") {
                    color = Color.white
                }
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

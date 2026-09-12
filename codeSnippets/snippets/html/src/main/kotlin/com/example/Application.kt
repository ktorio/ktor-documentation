package com.example

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.html.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        get("/") {
            val name = "Ktor"
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +name
                    }
                }
                body {
                    h1 {
                        +"Hello from $name!"
                    }
                }
            }
        }
        get("/fragment") {
            call.respondHtmlFragment(HttpStatusCode.Created) {
                div("fragment") {
                    span { +"Created!" }
                }
            }
        }
        get("/stream") {
            call.respondTextWriter(ContentType.Text.Plain) {
                for (i in 1..5) {
                    appendLine("line-$i")
                    flush()
                }
            }
        }
        get("/stream-bytes") {
            call.respondOutputStream(ContentType.Application.OctetStream) {
                for (i in 1..5) {
                    write("jvm chunk-$i\n".toByteArray())
                    flush()
                }
            }
        }
        get("/stream-channel") {
            call.respondBytesWriter(ContentType.Application.OctetStream) {
                for (i in 1..5) {
                    writeStringUtf8("kmp chunk-$i\n")
                    flush()
                }
            }
        }
    }
}

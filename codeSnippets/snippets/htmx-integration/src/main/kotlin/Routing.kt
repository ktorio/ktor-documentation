package com.example

import io.ktor.htmx.HxSwap
import io.ktor.htmx.html.hx
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.htmx.hx
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

@OptIn(ExperimentalKtorApi::class)
fun Application.configureHtmx() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    // Include HTMX library
                    script(src = "https://unpkg.com/htmx.org@1.9.10") {}
                }
                body {
                    div {
                        id = "content"
                        h1 { +"HTMX Demo" }

                        button {
                            id = "load-button"
                            attributes.hx {
                                get = "/data"
                                target = "#result"
                                swap = HxSwap.innerHtml
                                trigger = "click"
                            }
                            +"Load Data"
                        }

                        div {
                            id = "result"
                            +"Data will appear here"
                        }
                    }
                }
            }
        }

        route("data") {
            // Regular request
            get {
                call.respondText("Please use HTMX to load this data")
            }

            // Any HTMX request
            hx.get {
                call.respondText("<p>Data loaded via HTMX!</p>")
            }

            // HTMX request with specific target
            hx {
                target("#result") {
                    get {
                        call.respondText("<p>Data specifically for #result element</p>")
                    }
                }

                // HTMX request with specific trigger
                trigger("#load-button") {
                    get {
                        call.respondText("<p>Data loaded by #load-button</p>")
                    }
                }
            }
        }
    }
}
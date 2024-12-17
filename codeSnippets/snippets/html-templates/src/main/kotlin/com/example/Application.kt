package com.example

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        get("/") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                header {
                    +"Ktor"
                }
                content {
                    articleTitle {
                        +"Hello from Ktor!"
                    }
                    articleText {
                        +"Kotlin Framework for creating connected systems."
                    }
                    list {
                        item { +"One" }
                        item { +"Two" }
                    }
                }
            }
        }
    }
}

class LayoutTemplate: Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<ArticleTemplate>()
    override fun HTML.apply() {
        body {
            h1 {
                insert(header)
            }
            insert(ArticleTemplate(), content)
        }
    }
}

class ArticleTemplate : Template<FlowContent> {
    val articleTitle = Placeholder<FlowContent>()
    val articleText = Placeholder<FlowContent>()
    val list = TemplatePlaceholder<ListTemplate>()
    override fun FlowContent.apply() {
        article {
            h2 {
                insert(articleTitle)
            }
            p {
                insert(articleText)
            }
            insert(ListTemplate(), list)
        }
    }
}

class ListTemplate : Template<FlowContent> {
    val item = PlaceholderList<UL, FlowContent>()
    override fun FlowContent.apply() {
        if (!item.isEmpty()) {
            ul {
                each(item) {
                    li {
                        if (it.first) {
                            b {
                                insert(it)
                            }
                        } else {
                            insert(it)
                        }
                    }
                }
            }
        }
    }
}
package com.example

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.resources.Resource
import io.ktor.server.resources.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.*

@Resource("/")
class Index()

@Resource("/number")
class Number(val value: Int)

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Resources)
    routing {
        get<Index> {
            call.respondHtml {
                head {
                    title { +"Ktor: locations" }
                }
                body {
                    p {
                        +"Hello from Ktor locations sample application"
                    }
                    h1 {
                        +"Choose a Number"
                    }
                    ul {
                        val rnd = Random()
                        (0..5).forEach {
                            li {
                                val number = Number(rnd.nextInt(1000))
                                a(href = href(number)) {
                                    +"Number #${number.value}"
                                }
                            }
                        }
                    }
                }
            }
        }

        get<Number> { number ->
            call.respondHtml {
                head {
                    title { +"Ktor: locations" }
                }
                body {
                    h1 {
                        +"Number is ${number.value}"
                    }
                }
            }
        }
    }
}

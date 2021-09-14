package io.ktor.snippets.locations

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.html.*
import io.ktor.server.locations.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.*

@Location("/") class index()
@Location("/number") class number(val value: Int)

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    routing {
        get<index> {
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
                                val number = number(rnd.nextInt(1000))
                                a(href = locations.href(number)) {
                                    +"Number #${number.value}"
                                }
                            }
                        }
                    }
                }
            }
        }

        get<number> { number ->
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

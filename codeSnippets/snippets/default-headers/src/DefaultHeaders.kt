package io.ktor.snippets.defaultheaders

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "Custom server")
        header("Custom-Header", "Some value")
    }
    routing {
        get("/") {
            var headers = ""
            call.response.headers.allValues().forEach { header, values ->
                headers = headers + "$header: ${values.firstOrNull()}" + "\n"
            }
            call.respondText(headers)
        }
    }
}

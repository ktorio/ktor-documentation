package com.example.plugins

import io.ktor.client.plugins.api.*

val CustomHeaderPlugin = createClientPlugin("CustomHeaderPlugin") {
    onRequest { request, _ ->
        request.headers.append("X-Custom-Header", "Default value")
    }
}

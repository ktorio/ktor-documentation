package com.example.plugins

import io.ktor.client.plugins.api.*

val LoggingHeadersPlugin = createClientPlugin("LoggingHeadersPlugin") {
    on(SendingRequest) { request, content ->
        println("Request headers:")
        request.headers.entries().forEach { entry ->
            printHeader(entry)
        }
    }

    onResponse { response ->
        println("Response headers:")
        response.headers.entries().forEach { entry ->
            printHeader(entry)
        }
    }
}

private fun printHeader(entry: Map.Entry<String, List<String>>) {
    var headerString = entry.key + ": "
    entry.value.forEach { headerValue ->
        headerString += "${headerValue};"
    }
    println("-> $headerString")
}

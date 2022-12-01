package com.example.plugins

import com.example.model.*
import io.ktor.client.plugins.api.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*

val DataTransformationPlugin = createClientPlugin("DataTransformationPlugin") {
    transformRequestBody { request, content, bodyType ->
        if (bodyType?.type == User::class) {
            val user = content as User
            TextContent(text="${user.name};${user.age}", contentType = ContentType.Text.Plain)
        } else {
            null
        }
    }
    transformResponseBody { response, content, requestedType ->
        if (requestedType.type == User::class) {
            val receivedContent = content.readUTF8Line()!!.split(";")
            User(receivedContent[0], receivedContent[1].toInt())
        } else {
            content
        }
    }
}

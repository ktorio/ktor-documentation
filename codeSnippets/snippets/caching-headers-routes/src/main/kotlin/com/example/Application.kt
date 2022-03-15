package com.example

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        route("/index") {
            install(CachingHeaders) {
                options { call -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600)) }
            }
            get {
                call.respondText("Index page")
            }
        }
        route("/profile") {
            install(CachingHeaders) {
                options { call -> CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private)) }
            }
            get {
                call.respondText("Profile page")
            }
        }
    }
}

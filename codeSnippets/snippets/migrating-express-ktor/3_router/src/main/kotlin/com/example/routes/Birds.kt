package com.example.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.birdsRoutes() {
    route("/birds") {
        get {
            call.respondText("Birds home page")
        }
        get("/about") {
            call.respondText("About birds")
        }
    }
}
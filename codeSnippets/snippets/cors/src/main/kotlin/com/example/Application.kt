package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.*

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

fun main() {
    val appProperties = applicationProperties {
        module {
            backendModule()
            frontendModule()
        }
    }
    embeddedServer(Netty, appProperties) {
        envConfig()
    }.start(true)
}

fun ApplicationEngine.Configuration.envConfig() {
    connector {
        host = "0.0.0.0"
        port = 8080
    }
    connector {
        host = "0.0.0.0"
        port = 8081
    }
}

fun Application.backendModule() {
    install(CORS) {
        allowHost("0.0.0.0:8081")
        allowHeader(HttpHeaders.ContentType)
    }
    val customerStorage = mutableListOf<Customer>()
    install(ContentNegotiation) {
        json(Json)
    }
    routing {
        port(8080) {
            post("/customer") {
                val customer = call.receive<Customer>()
                customerStorage.add(customer)
                call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
            }
        }
    }
}

fun Application.frontendModule() {
    routing {
        port(8081) {
            get("/") {
                call.respondHtml {
                    head {
                        script(src = "/static/script.js") {}
                    }
                    body {
                        button {
                            onClick = "saveCustomer()"
                            + "Make a CORS request to save a customer"
                        }
                    }
                }
            }
            staticFiles("static", File("files"), "js/script.js")
        }
    }
}

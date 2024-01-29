import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.*
import kotlinx.coroutines.*
import kotlinx.html.*
import kotlinx.html.div
import kotlinx.html.dom.*
import kotlinx.html.js.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.coroutines.*

fun main() {
    document.addEventListener("DOMContentLoaded", {
        Application().start()
    })
}

class Application : CoroutineScope {
    private val body get() = document.body!!
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    fun start() {
        body.append.div("content") {
            div {
                button {
                    +"Save a customer"
                    onClickFunction = {
                        var responseText: String = ""
                        val job = launch {
                            responseText = saveCustomer()
                        }
                        job.invokeOnCompletion {
                            println(responseText)
                            window.alert(responseText)
                        }
                    }
                }
            }
        }
    }
}

suspend fun saveCustomer(): String {
    val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
    }
    val response: HttpResponse = client.post("http://0.0.0.0:8080/customer") {
        contentType(ContentType.Application.Json)
        setBody(Customer(3, "Jet", "Brains"))
    }
    return response.bodyAsText()
}

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)

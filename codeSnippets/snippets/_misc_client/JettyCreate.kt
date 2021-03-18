import io.ktor.client.*
import io.ktor.client.engine.jetty.*

val client = HttpClient(Jetty)
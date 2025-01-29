import io.ktor.client.*
import io.ktor.client.engine.jetty.jakarta.*

val client = HttpClient(Jetty)
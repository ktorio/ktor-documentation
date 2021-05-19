import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*

val client = HttpClient(CIO) {
    install(Logging)
}
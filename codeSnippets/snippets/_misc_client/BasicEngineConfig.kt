import io.ktor.client.*
import io.ktor.client.engine.cio.*

val client = HttpClient(CIO) {
    engine {
        // Configure an engine
    }
}
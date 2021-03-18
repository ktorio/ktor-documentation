import io.ktor.client.*
import io.ktor.client.engine.curl.*

val client = HttpClient(Curl) {
    engine {
        // this: CurlClientEngineConfig
        sslVerify = true
    }
}
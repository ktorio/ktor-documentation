import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.java.*

val client = HttpClient(Java) {
    engine {
        // this: [[[JavaHttpConfig|https://api.ktor.io/%ktor_version%/io.ktor.client.engine.java/-java-http-config/index.html]]]
        threadsCount = 8
        pipelining = true
        proxy = ProxyBuilder.http("http://proxy-server.com/")
    }
}
import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import org.apache.hc.core5.http.*

val client = HttpClient(Apache5)

val client = HttpClient(Apache5) {
    engine {
        // this: Apache5EngineConfig
        followRedirects = true
        socketTimeout = 10_000
        connectTimeout = 10_000
        connectionRequestTimeout = 20_000
        customizeClient {
            // this: HttpAsyncClientBuilder
            setProxy(HttpHost("127.0.0.1", 8080))
            // ...
        }
        customizeRequest {
            // this: RequestConfig.Builder
        }
    }
}

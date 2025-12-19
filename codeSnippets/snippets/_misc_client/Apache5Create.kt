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

        // Configure the Apache5 ConnectionManager
        configureConnectionManager {
            setMaxConnPerRoute(1_000)
            setMaxConnTotal(2_000)
        }

        // Customize the underlying Apache client for other settings
        customizeClient {
            // this: HttpAsyncClientBuilder
            setProxy(HttpHost("127.0.0.1", 8080))
            // ...
        }

        // Customize per-request settings
        customizeRequest {
            // this: RequestConfig.Builder
        }
    }
}

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import org.apache.http.HttpHost

val client = HttpClient(Apache) {
    engine {
        // this: [[[ApacheEngineConfig|https://api.ktor.io/ktor-client-apache/io.ktor.client.engine.apache/-apache-engine-config/index.html]]]
        followRedirects = true
        socketTimeout = 10_000
        connectTimeout = 10_000
        connectionRequestTimeout = 20_000
        customizeClient {
            // this: HttpAsyncClientBuilder
            setProxy(HttpHost("127.0.0.1", 8080))
            setMaxConnTotal(1000)
            setMaxConnPerRoute(100)
            // ...
        }
        customizeRequest {
            // this: RequestConfig.Builder
        }
    }
}
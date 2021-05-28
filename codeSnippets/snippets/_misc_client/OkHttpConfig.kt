import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

val client = HttpClient(OkHttp) {
    engine {
        // this: [[[https://api.ktor.io/ktor-client/ktor-client-okhttp/ktor-client-okhttp/io.ktor.client.engine.okhttp/-ok-http-config/index.html]]]
        config {
            // this: OkHttpClient.Builder
            followRedirects(true)
            // ...
        }
        addInterceptor(interceptor)
        addNetworkInterceptor(interceptor)

        preconfigured = okHttpClientInstance
    }
}
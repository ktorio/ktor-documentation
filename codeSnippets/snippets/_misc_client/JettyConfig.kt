import io.ktor.client.*
import io.ktor.client.engine.jetty.jakarta.*
import org.eclipse.jetty.util.ssl.SslContextFactory

val client = HttpClient(Jetty) {
    engine {
        // this: [[[JettyEngineConfig|https://api.ktor.io/ktor-client-jetty-jakarta/io.ktor.client.engine.jetty.jakarta/-jetty-engine-config/index.html]]]
        sslContextFactory = SslContextFactory.Client()
        clientCacheSize = 12
    }
}
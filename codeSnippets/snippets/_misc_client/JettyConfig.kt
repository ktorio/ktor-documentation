import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import org.eclipse.jetty.util.ssl.SslContextFactory

val client = HttpClient(Jetty) {
    engine {
        // this: [[[JettyEngineConfig|https://api.ktor.io/%ktor_version%/io.ktor.client.engine.jetty/-jetty-engine-config/index.html]]]
        sslContextFactory = SslContextFactory.Client()
        clientCacheSize = 12
    }
}
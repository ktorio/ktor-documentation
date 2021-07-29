import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.network.tls.*

val client = HttpClient(CIO) {
    engine {
        // this: [[[CIOEngineConfig|https://api.ktor.io/ktor-client/ktor-client-cio/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o-engine-config/index.html]]]
        maxConnectionsCount = 1000
        endpoint {
            // this: [[[EndpointConfig|https://api.ktor.io/ktor-client/ktor-client-cio/ktor-client-cio/io.ktor.client.engine.cio/-endpoint-config/index.html]]]
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }
        https {
            // this: [[[TLSConfigBuilder|https://api.ktor.io/ktor-network/ktor-network-tls/ktor-network-tls/io.ktor.network.tls/-t-l-s-config-builder/index.html]]]
            serverName = "api.ktor.io"
            cipherSuites = CIOCipherSuites.SupportedSuites
            trustManager = myCustomTrustManager
            random = mySecureRandom
            addKeyStore(myKeyStore, myKeyStorePassword)
        }
    }
}
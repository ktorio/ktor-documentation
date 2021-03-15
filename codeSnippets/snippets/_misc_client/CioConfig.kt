val client = HttpClient(CIO) {
    engine {
        maxConnectionsCount = 1000
        endpoint {
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }
        https {
            serverName = "api.ktor.io"
            cipherSuites = CIOCipherSuites.SupportedSuites
            trustManager = myCustomTrustManager
            random = mySecureRandom
        }
    }
}
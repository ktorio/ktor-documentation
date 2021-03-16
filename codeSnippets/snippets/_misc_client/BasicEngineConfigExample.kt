HttpClient() {
    engine {
        // this: [[[HttpClientEngineConfig|https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html]]]
        threadsCount = 4
        pipelining = true
    }
}
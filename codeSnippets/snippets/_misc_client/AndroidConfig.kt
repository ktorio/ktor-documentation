val client = HttpClient(Android) {
    engine {
        connectTimeout = 100_000
        socketTimeout = 100_000
        proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("localhost", serverPort))
    }
}
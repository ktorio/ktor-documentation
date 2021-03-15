val client = HttpClient(Java) {
    engine {
        threadsCount = 8
        pipelining = true
        proxy = ProxyBuilder.http("http://proxy-server.com/")
    }
}
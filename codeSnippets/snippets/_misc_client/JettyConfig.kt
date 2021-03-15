val client = HttpClient(Jetty) {
    engine {
        sslContextFactory = SslContextFactory()
    }
}
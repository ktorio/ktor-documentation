val client = HttpClient(OkHttp) {
    engine {
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.Builder.html
        config { // this: OkHttpClient.Builder ->
            followRedirects(true)
            // ...
        }
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/Interceptor.html
        addInterceptor(interceptor)
        addNetworkInterceptor(interceptor)

        preconfigured = okHttpClientInstance
    }
}
[//]: # (title: Engines)

<include src="lib.md" include-id="outdated_warning"/>

Ktor HTTP Client has a common interface but allows to specify an engine that processes the network request. Different engines have different configurations, dependencies and supporting features.

## Adding an engine dependency {id="dependencies"}

The first thing you need to do before using the client is to add a client engine dependency. Supported engines and required dependencies are listed below: 
* [`Apache`](#apache): `ktor-client-apache`
* [`OkHttp`](#okhttp): `ktor-client-okhttp`
* [`Android`](#android): `ktor-client-android`
* [`Ios`](#ios): `ktor-client-ios`
* [`Js`](#js-javascript): `ktor-client-js`
* [`Jetty`](#jetty): `ktor-client-jetty`
* [`CIO`](#cio): `ktor-client-cio`
* [`Mock`](#mock): `ktor-client-mock`

For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.md" include-id="add_ktor_artifact"/>



## Default engine {id="default"}

By calling to the `HttpClient` method without specifying an engine, it uses a default engine.

```kotlin
val client = HttpClient()
```

In the case of the JVM, the default engine is resolved with a ServiceLoader, getting the first one available sorted in alphabetical order.
Thus depends on the artifacts you have included.

For native, the engine detected during static linkage. Please provide one of the native engines in artifacts.

For js, it uses the predefined one.

## Configuring engines {id="configuring"}

Ktor HttpClient lets you configure the parameters of each engine by calling:

```kotlin
HttpClient(MyHttpEngine) {
    engine {
        // this: MyHttpEngineConfig
    }
}
```

Every engine config has some common properties that can be set:

* The `threadsCount` property is a recommendation to use by an engine. It can be ignored if an engine doesn't require such amount of threads.
* The `pipelining` is an experimental flag to enable [HTTP pipelining](https://en.wikipedia.org/wiki/HTTP_pipelining).

```kotlin
val client = HttpClient(MyHttpEngine) {
    engine {
        threadsCount = 4
        pipelining = true
    }
}
```

## JVM {id="jvm"}

### Apache {id="apache"}

Apache HTTP client supports HTTP/1.1 and provides multiple configuration options. It is the only one that supports following redirects and allows you to configure timeouts, proxies among other things it is supported by `org.apache.httpcomponents:httpasyncclient`.

A sample configuration would look like:

```kotlin
val client = HttpClient(Apache) {
    engine {
        /**
         * Apache embedded http redirect, default = false. Obsolete by `HttpRedirect` feature.
         * It uses the default number of redirects defined by Apache's HttpClient that is 50.
         */
        followRedirects = true

        /**
         * Timeouts.
         * Use `0` to specify infinite.
         * Negative value mean to use the system's default value.
         */

        /**
         * Max time between TCP packets - default 10 seconds.
         */
        socketTimeout = 10_000

        /**
         * Max time to establish an HTTP connection - default 10 seconds.
         */
        connectTimeout = 10_000

        /**
         * Max time for the connection manager to start a request - 20 seconds.
         */
        connectionRequestTimeout = 20_000

        customizeClient {
            // this: HttpAsyncClientBuilder
            setProxy(HttpHost("127.0.0.1", 8080))

            // Maximum number of socket connections.
            setMaxConnTotal(1000)

            // Maximum number of requests for a specific endpoint route.
            setMaxConnPerRoute(100)

            // ...
        }
        customizeRequest {
            // this: RequestConfig.Builder from Apache.
        }
    }
}
```



### CIO {id="cio"}

CIO (Coroutine-based I/O) is a Ktor implementation with no additional dependencies and is fully asynchronous.
It only supports HTTP/1.x for now.

CIO provides `maxConnectionsCount` and a `endpointConfig` for configuring.

A sample configuration would look like:

```kotlin
val client = HttpClient(CIO) {
    engine {
        /**
         * Maximum number of socket connections.
         */
        maxConnectionsCount = 1000

        /**
         * Endpoint specific settings.
         */
        endpoint {
            /**
             * Maximum number of requests for a specific endpoint route.
             */
            maxConnectionsPerRoute = 100

            /**
             * Max size of scheduled requests per connection(pipeline queue size).
             */
            pipelineMaxSize = 20

            /**
             * Max number of milliseconds to keep iddle connection alive.
             */
            keepAliveTime = 5000

            /**
             * Number of milliseconds to wait trying to connect to the server.
             */
            connectTimeout = 5000

            /**
             * Maximum number of attempts for retrying a connection.
             */
            connectAttempts = 5
        }

        /**
         * Https specific settings.
         */
        https {
            /**
            * Custom server name for TLS server name extension.
             * See also: https://en.wikipedia.org/wiki/Server_Name_Indication
             */
            serverName = "api.ktor.io"

            /**
             * List of allowed [CipherSuite]s.
             */
            cipherSuites = CIOCipherSuites.SupportedSuites

            /**
             * Custom [X509TrustManager] to verify server authority.
             *
             * Use system by default.
             */
            trustManager = myCustomTrustManager

            /**
             * [SecureRandom] to use in encryption.
             */
            random = mySecureRandom
        }
    }
}
```



### Jetty {id="jetty"}

Jetty provides an additional `sslContextFactory` for configuring. It only supports HTTP/2 for now.

A sample configuration would look like:

```kotlin
val client = HttpClient(Jetty) {
    engine {
        sslContextFactory = SslContextFactory()
    }
}
```



## JVM and Android

### OkHttp {id="okhttp"}

There is a engine based on OkHttp:

```kotlin
val client = HttpClient(OkHttp) {
    engine {
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.Builder.html
        config { // this: OkHttpClient.Builder ->
            // ...
            followRedirects(true)
            // ...
        }

        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/Interceptor.html
        addInterceptor(interceptor)
        addNetworkInterceptor(interceptor)

        /**
         * Set okhttp client instance to use instead of creating one.
         */
        preconfigured = okHttpClientInstance
    }
}
```



### Android {id="android"}

The Android engine doesn't have additional dependencies and uses a ThreadPool with a normal HttpURLConnection,
to perform the requests. And can be configured like this:

```kotlin
val client = HttpClient(Android) {
    engine {
        connectTimeout = 100_000
        socketTimeout = 100_000

        /**
         * Proxy address to use.
         */
        proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("localhost", serverPort))
    }
}
```



## iOS {id="ios"}

The iOS engine uses the asynchronous `NSURLSession` internally. And have no additional configuration.

```kotlin
val client = HttpClient(Ios) {
    /**
     * Configure native NSUrlRequest.
     */
    configureRequest { // this: NSMutableURLRequest
        setAllowsCellularAccess(true)
        // ...
    }
}
```



## Js (JavaScript) {id="js-javascript"}

The `Js` engine, uses the [`fetch`](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) API internally(and `node-fetch` for node.js runtime).

Js engine has no custom configuration.

```kotlin
val client = HttpClient(Js) {
}
```

You can also call the `JsClient()` function to get the `Js` engine singleton.



## Curl {id="curl"}

There is an engine based on Curl:

```kotlin
val client = HttpClient(Curl)
```

Supported platforms: linux_x64, macos_x64, mingw_x64. Please note that to use the engine you must have the installed curl library at least version 7.63



### MockEngine {id="mock"}

The `MockEngine` is the common engine for testing. See also [MockEngine for testing](http-client_testing.md).

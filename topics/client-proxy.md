[//]: # (title: Proxy)

<show-structure for="chapter" depth="2"/>

Ktor HTTP client allows you to configure proxy settings in multiplatform projects. 
There are two supported types of proxies: [HTTP](https://en.wikipedia.org/wiki/Proxy_server#Web_proxy_servers) and [SOCKS](https://en.wikipedia.org/wiki/SOCKS).

### Supported engines {id="supported_engines"}

The table below shows supported proxy types for specific [engines](client-engines.md):

| Engine     | HTTP proxy | SOCKS proxy |
|------------|------------|-------------|
| Apache     | ✅          |   ✖️         |
| Java       | ✅          |   ✖️         |
| Jetty      | ✖️          |   ✖️         |
| CIO        | ✅          |   ✖️         |
| Android    | ✅          |   ✅         |
| OkHttp     | ✅          |   ✅         |
| JavaScript | ✖️          |   ✖️         |
| Darwin     | ✅          |   ✖️          |
| Curl       | ✅          |   ✅         |

> Note that HTTPS requests are currently not supported with the HTTP proxy for the Darwin engine.

## Add dependencies {id="add_dependencies"}

To configure the proxy in the client, you don't need to add a specific dependency. The required dependencies are:
- [ktor-client-core](client-dependencies.md#client-dependency);
- [an engine dependency](client-dependencies.md#engine-dependency).


## Configure proxy {id="configure_proxy"}

To configure proxy settings, call the `engine` function inside a [client configuration block](client-create-and-configure.md#configure-client) and then use the `proxy` property.
This property accepts the `ProxyConfig` instance that can be created using the [ProxyBuilder](https://api.ktor.io/ktor-client-core/io.ktor.client.engine/-proxy-builder/index.html) factory.

```kotlin
val client = HttpClient() {
    engine {
        proxy = // Create proxy configuration
    }
}
```

### HTTP proxy {id="http_proxy"}

The example below shows how to configure HTTP proxy using `ProxyBuilder`:

```kotlin
val client = HttpClient() {
    engine {
        proxy = ProxyBuilder.http("http://sample-proxy-server:3128/")
    }
}
```

On JVM, `ProxyConfig` is mapped to the [Proxy](https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Proxy.html) class, so you can configure the proxy as follows:

```kotlin
val client = HttpClient() {
    engine {
        proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("sample-proxy-server", 3128))
    }
}
```




### SOCKS proxy {id="socks_proxy"}

The example below shows how to configure SOCKS proxy using `ProxyBuilder`:

```kotlin
val client = HttpClient() {
    engine {
        proxy = ProxyBuilder.socks(host = "sample-proxy-server", port = 1080)
    }
}
```

As for the HTTP proxy, on JVM you can use `Proxy` to configure proxy settings:

```kotlin
val client = HttpClient() {
    engine {
        proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("sample-proxy-server", 1080))
    }
}
```


## Proxy authentication and authorization {id="proxy_auth"}

Proxy authentication and authorization are engine-specific and should be handled manually.
For example, to authenticate a Ktor client to an HTTP proxy server using basic authentication, append the `Proxy-Authorization` header to [each request](client-default-request.md) as follows:

```kotlin
val client = HttpClient() {
    defaultRequest {
        val credentials = Base64.getEncoder().encodeToString("jetbrains:foobar".toByteArray())
        header(HttpHeaders.ProxyAuthorization, "Basic $credentials")
    }
}
```

To authenticate a Ktor client to a SOCKS proxy on JVM, you can use the `java.net.socks.username` and `java.net.socks.password` [system properties](https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html).

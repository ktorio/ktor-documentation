[//]: # (title: Proxy)

<include src="lib.md" include-id="outdated_warning"/>

Ktor HTTP client allows using proxy in multiplatform code. The following document describes how to configure a proxy in ktor.

## Multiplatform configuration

### Create proxy

You don't need to include additional artifacts to create a proxy. The supported proxy types are specific to a client engine. Two types of proxy can be configured in multiplatform: [HTTP](https://en.wikipedia.org/wiki/Proxy_server#Web_proxy_servers) and [SOCKS](https://en.wikipedia.org/wiki/SOCKS).

To create a proxy configuration use builders in the _ProxyBuilder_ factory:
```kotlin
// Create http proxy
val httpProxy = ProxyBuilder.http("http://my-proxy-server-url.com/")

// Create socks proxy
val socksProxy = ProxyBuilder.socks(host = "127.0.0.1", port = 4001)
```

Proxy authentication and authorization are engine specific and should be handled by the user manually.

### Set proxy

Proxy can be configured in multiplatform code using _ProxyConfig_ builder in _HttpClientEngineConfig_ block:
```kotlin
val client = HttpClient() {
    engine {
        proxy = httpProxy
    }
}
```

## Platform-specific configuration

### Jvm

The _ProxyConfig_ class maps to [Proxy](https://docs.oracle.com/javase/7/docs/api/java/net/Proxy.html) class on the jvm:
```kotlin
val httpProxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(4040))
```

Most Jvm client engines support it out of the box.

Note: Apache and CIO engines support HTTP proxy only. Jetty client engine doesn't support any proxy.

### Native

The native _ProxyConfig_ class can use url to determine proxy address:
```kotlin
val socksProxy = ProxyConfig(url = "socks://my-socks-proxy.com/")
```

Supported proxy types are engine specific. To see supported URLs consult with engine provider documentation:

- Curl: [https://curl.haxx.se/libcurl/c/CURLOPT_PROXY.html](https://curl.haxx.se/libcurl/c/CURLOPT_PROXY.html)
- iOS: [https://developer.apple.com/documentation/foundation/nsurlsessionconfiguration/1411499-connectionproxydictionary](https://developer.apple.com/documentation/foundation/nsurlsessionconfiguration/1411499-connectionproxydictionary)

### Js

The proxy configuration is unsupported by platform restrictions.

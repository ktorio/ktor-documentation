[//]: # (title: What's new in Ktor 3.6.0)

<show-structure for="chapter,procedure" depth="3"/>

[//]: # (TODO: Ensure release date is correct)
_[Released: August 26, 2026](releases.md#release-details)_

Ktor 3.6.0 delivers a range of improvements across server and client. Highlights of this feature release include:

[//]: # (TODO: Add a bullet list with highlights)

## Ktor Server

### Additional type support for request parameters

Ktor 3.6.0 expands the set of types supported by default when converting request parameters to typed values.

The following types are now supported:

* `Uuid`
* `Byte`
* `java.lang.Byte`
* `UByte`
* `UShort`
* `UInt`
* `ULong`

For example, you can retrieve a `Uuid` parameter directly inside a route handler through property delegation:

```kotlin
get {
    val uuid: Uuid by call.parameters
}
```

### OpenAPI tag descriptions

You can now define descriptions for OpenAPI tags directly in the [`openAPI {}`](server-openapi.md) and [`swaggerUI {}`](server-swagger-ui.md)
configuration blocks:

```kotlin
swaggerUI("/swagger") {
    info = OpenApiInfo("Books API from routes", "1.0.0")
    tag(
        name = "Books",
        description = "Operations on books"
    )
}
```

The tag description is added to the top-level metadata of the generated OpenAPI document.

### New `ApplicationCall.respondHtmlPartial()` function

The new `.respondHtmlPartial()` function replaces `.respondHtmlFragment()` for responding
with partial HTML content.

It uses `TagConsumer<Appendable>` as the lambda receiver, which allows you to return unrestricted HTML content,
such as table cells:

```kotlin
call.respondHtmlPartial(HttpStatusCode.Created) {
    td { +"Created!" } 
}
```

The previous `.respondHtmlFragment()` function uses `FlowContent`, which restricts the HTML elements that can be returned.
It is now deprecated in favor of `.respondHtmlPartial()`.

### Netty

#### HTTP/3 support

The Netty server engine now includes experimental support for [](server-http3.md) over QUIC.

To enable HTTP/3, configure an SSL connector and call the `enableHttp3()` function in the Netty engine configuration:

```kotlin
embeddedServer(Netty, environment, {
    // SSL connector is required
    sslConnector(
        keyStore = keyStore,
        keyAlias = "server",
        keyStorePassword = { "changeit".toCharArray() },
        privateKeyPassword = { "changeit".toCharArray() }
    ) {
        port = 8443
        host = "0.0.0.0"
    }

    enableHttp3 {
        quicTokenHandler = HmacQuicTokenHandler() // Optional
        quicMaxIdleTimeout = 30.seconds
        quicInitialMaxData = 10_000_000
        quicInitialMaxStreamDataBidirectionalLocal = 1_000_000
        quicInitialMaxStreamDataBidirectionalRemote = 1_000_000
        quicInitialMaxStreamsBidirectional = 100
        udpSocketCount = 1
        udpReceiveBufferSize = 0
        udpSendBufferSize = 0
        configureQuicServerCodec = { /* Optional low-level Netty tuning */ }
    }
}) { /* Application */ }.start(wait = true)
```

You can also use the `enableHttp3 {}` block to configure QUIC-specific options such as connection timeouts,
flow-control limits, and UDP socket settings.

#### Use h2c alongside HTTP/2 over TLS

The Netty server engine can now serve [HTTP/2 over cleartext (h2c)](server-http2.md#h2c) and HTTP/2 over TLS on the
same server.

This allows you to configure a cleartext connector and an SSL connector, then enable both HTTP/2 and h2c:

```kotlin
embeddedServer(Netty, configure = {
    connector {
        port = 8080
    }
    sslConnector(...) {
        port = 8443
    }

    enableHttp2 = true
    enableH2c = true
}) {
    // ...
}
```

The cleartext connector accepts h2c connections, while the SSL connector serves HTTP/2 over TLS.

## Ktor Client

### Default client engines for multiplatform projects

Ktor 3.6.0 introduces the `ktor-client-engine-defaults` artifact, which provides a curated set of HTTP [client engines](client-engines.md)
for [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/get-started.html) projects.

Add the dependency to the `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("io.ktor:ktor-client-engine-defaults:3.6.0")
            }
        }
    }
}
```

You can then create an `HttpClient` without specifying an engine:

```kotlin
val client = HttpClient()
```

For each target platform, Ktor uses the default engine provided by `ktor-client-engine-defaults`. If more than one engine
is available, the client selects the engine with the highest priority. `CIO` has the lowest priority by default, so the
client selects another available engine over `CIO`.

If your multiplatform project currently uses `CIO` across all supported targets, consider replacing the `CIO` dependency
with `ktor-client-engine-defaults`. This lets Ktor provide a curated default engine for each platform while keeping
engine selection out of your common source set.

You can still [declare a specific client engine](client-dependencies.md#kmp-specific-engine) when you need engine-specific
configuration or behavior.

### WebRTC client support for JVM
<primary-label ref="experimental"/>

The experimental [WebRTC client](client-webrtc.md) now supports JVM desktop applications.

The JVM implementation uses [webrtc-java](https://github.com/devopvoid/webrtc-java) native WebRTC bindings and provides
support for peer connections, audio and video tracks, data channels, and connection statistics.

JVM support currently has several platform-specific limitations. For more information, see the
[WebRTC client](client-webrtc.md) documentation.

### Multiplatform file storage for HTTP caching

The [`HttpCache`](client-caching.md) plugin now supports multiplatform file storage.

Previously, the `FileStorage()` function was available only on JVM and required a `java.io.File`. It now uses
`kotlinx-io`, which allows you to configure persistent file-based caching on any supported platform using `Path`.

<compare type="top-bottom" first-title="3.5.x" second-title="3.6.0">

```kotlin
val client = HttpClient {
    install(HttpCache) {
        val cacheFile = Files.createDirectories(Paths.get("build/cache")).toFile()
        publicStorage(FileStorage(cacheFile))
    }
}
```

```kotlin
val client = HttpClient {
    install(HttpCache) {
        publicStorage(FileStorage(Path("build/cache")))
    }
}
```

</compare>

This replaces the JVM-specific setup that creates a `File` before passing it to `FileStorage()`.

### Control `Accept` header merging in `ContentNegotiation`

You can now control how the client [`ContentNegotiation`](client-serialization.md) plugin merges registered content types
with an existing `Accept` header.

By default, the `ContentNegotiation` plugin adds registered content types that aren't already represented in the request's
`Accept` header.

If you set an `Accept` header explicitly and don't want the plugin to add registered content types, set
the `acceptHeaderMergeStrategy` property to `ContentTypeMergeStrategy.SkipIfPresent`:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, noOpJsonConverter)
    acceptHeaderMergeStrategy = ContentTypeMergeStrategy.SkipIfPresent
}
```

With `SkipIfPresent`, the plugin preserves an existing `Accept` header. If the request doesn't contain an `Accept` header,
the plugin adds the registered content types as usual.

### Asynchronous DNS resolution in the CIO client engine

This release adds support for custom DNS resolution in the [`CIO` client engine](client-engines.md#cio).

On JVM, the `CIO` engine previously relied on system DNS resolution, which can block threads. You can now override DNS
resolution using the `dnsResolver` property in the `CIO` engine configuration.

For example, use the `CioDnsResolver()` function to resolve hostnames asynchronously through a specific DNS server and
configure a timeout:

```kotlin
HttpClient(CIO) {
    engine {
        dnsResolver = CioDnsResolver(
            server = "1.1.1.1",
            timeout = 3.seconds
        )
    }
}
```

### Override `fetch()` in the JavaScript client engine

You can now override the global `fetch()` function used by the [JavaScript client engine](client-engines.md#js).

To provide a custom implementation, set the `fetch` property in the `Js` engine configuration:

```kotlin
val client = HttpClient(Js) {
    engine {
        fetch = { url, init ->
            Promise.reject(IllegalStateException("Networking not available"))
        }
    }
}
```

This is useful when integrating with JavaScript libraries that provide their own `fetch()` wrapper, such as [AWS WAF](https://aws.amazon.com/waf/).
If you don't configure `fetch`, the engine continues to use the global `fetch()` function.
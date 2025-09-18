[//]: # (title: What's new in Ktor 3.3.0)

<show-structure for="chapter,procedure" depth="2"/>

_[Released: September 11, 2025](releases.md#release-details)_

Ktor 3.3.0 delivers new capabilities across server, client, and tooling. Here are the highlights for this feature
release:

* [Custom fallback mechanism for static resources](#custom-fallback)
* [OpenAPI specification generation](#openapi-spec-gen)
* [HTTP/2 cleartext (h2c) support](#http2-h2c-support)
* [Experimental WebRTC client](#webrtc-client)

## Ktor Server

### Custom fallback for static resources {id="custom-fallback"}

Ktor 3.3.0 introduces a new `fallback()` function for static content, allowing you to define custom
behavior when a requested resource is not found.

Unlike `default()`, which always serves the same fallback file, `fallback()` gives you access to the original requested
path and the current `ApplicationCall`. You can use this to redirect, return custom status codes, or serve different
files dynamically.

To define custom fallback behaviour, use the `fallback()` function within `staticFiles()`, `staticResources()`, `staticZip()`, or
`staticFileSystem()`:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="22,25-32,45"}

### Development mode auto-reload limitations

In Ktor 3.2.0, the introduced [support for suspend module functions](whats-new-320.md#suspendable-module-functions)
caused a regression where auto-reload stopped working for applications that use blocking module references.

This regression remains in 3.3.0 and auto-reload continues to work only with `suspend` function modules and
configuration references.

Examples of supported module declarations:

```kotlin
// Supported — suspend function reference
embeddedServer(Netty, port = 8080, module = Application::mySuspendModule)

// Supported — configuration reference (application.conf / application.yaml)
ktor {
    application {
        modules = [ com.example.ApplicationKt.mySuspendModule ]
    }
}
```

We plan to restore support for blocking function references in a future release. Until then, prefer a `suspend` module
or configuration reference in `development` mode.

### HTTP/2 cleartext (h2c) support {id="http2-h2c-support"}

Ktor 3.3.0 introduces support for HTTP/2 over cleartext (h2c) for the Netty engine,
which allows HTTP/2 communication without TLS encryption. 
This setup is typically used in trusted environments, such as local testing or private networks.

To enable h2c, set the `enableH2c` flag to true in the engine configuration.
For more information, see [HTTP/2 without TLS](server-http2.md#http-2-without-tls).

## Ktor Client

### SSE response body buffer

Until now, attempting to call `response.bodyAsText()` after an SSE error failed due to double-consume issues.

Ktor 3.3.0 introduces a configurable diagnostic buffer that allows you to capture already-processed SSE data for
debugging and error handling.

You can configure the buffer globally when installing the [SSE plugin](client-server-sent-events.topic):

```kotlin
install(SSE) {
    bufferPolicy = SSEBufferPolicy.LastEvents(10)
}
```

Or per call:

```kotlin
client.sse(url, { bufferPolicy(SSEBufferPolicy.All) }) {
    // …
}
```

As the SSE stream is consumed, the client maintains a snapshot of the processed data in an in-memory buffer (without
re-reading from the network). If an error occurs, you can safely call `response?.bodyAsText()` for logging or
diagnostics.

For more information, see [Response buffering](client-server-sent-events.topic#response-buffering).

### WebRTC client {id="webrtc-client"}

This release introduces experimental WebRTC client support for peer-to-peer real-time communication in multiplatform
projects.

WebRTC enables applications such as video calls, multiplayer gaming, and collaborative tools. With this release, you
can now use a unified Kotlin API to establish peer connections and exchange data channels across JavaScript/Wasm and
Android targets. Additional targets such as iOS, JVM desktop, and Native are planned for future releases.

You can create a `WebRtcClient` by selecting an engine for your platform and providing configuration, similar to
`HttpClient`:

<include from="client-webrtc.md" element-id="create-webrtc-client"/>

Once created, the client can establish peer-to-peer connections using Interactive Connectivity Establishment (ICE).
After negotiation completes, peers can open data channels and exchange messages.

```kotlin
val connection = client.createPeerConnection()

// Add a remote ICE candidate (received via your signaling channel)
connection.addIceCandidate(WebRtc.IceCandidate(candidateString, sdpMid, sdpMLineIndex))

// Wait until all local candidates are gathered
connection.awaitIceGatheringComplete()

// Listen for incoming data channel events
connection.dataChannelEvents.collect { event ->
   when (event) {
     is Open -> println("Another peer opened a chanel: ${event.channel}")
     is Closed -> println("Data channel is closed")
     is Closing, is BufferedAmountLow, is Error -> println(event)
   }
}

// Create a channel and send/receive messages
val channel = connection.createDataChannel("chat")
channel.send("hello")
val answer = channel.receiveText()
```

For more details on usage and limitations, see the [WebRTC client](client-webrtc.md) documentation.

### Updated OkHttp version

In Ktor 3.3.0, the Ktor client's `OkHttp` engine has been upgraded to use OkHttp 5.1.0 (previously 4.12.0). This major
version bump may introduce API changes for projects that interact directly with OkHttp. Such projects should verify
compatibility.

### Unified OkHttp SSE session

The OkHttp engine now uses the standard API for Server-Sent Events (SSE), 
replacing the previously introduced `OkHttpSSESession`.
This change unifies SSE handling across all client engines and addresses the limitations of the OkHttp-specific implementation.


## Gradle plugin

### OpenAPI specification generation {id="openapi-spec-gen"}
<secondary-label ref="experimental"/>

Ktor 3.3.0 introduces an experimental OpenAPI generation feature via the Gradle plugin and a compiler plugin. This
allows you to generate OpenAPI specifications directly from your application code at build time.

It provides the following capabilities:
- Analyze Ktor route definitions and merge nested routes, local extensions, and resource paths.
- Parse preceding KDoc annotations to supply OpenAPI metadata, including:
    - Path, query, header, cookie, and body parameters
    - Response codes and types
    - Security, descriptions, deprecations, and external documentation links
- Infer request and response bodies from `call.receive()` and `call.respond()`.


<include from="openapi-spec-generation.md" element-id="configure-the-extension"></include>

#### Generate the OpenAPI specification

To generate the OpenAPI specification file from your Ktor routes and KDoc annotations, use the following command:

```shell
./gradlew buildOpenApi
```

#### Serve the specification

To make the generated specification available at runtime, you can then use the [OpenAPI](server-openapi.md)
or [SwaggerUI](server-swagger-ui.md) plugins.

The following example serves the generated specification file at an OpenAPI endpoint:

```kotlin
routing {
    openAPI("/docs", swaggerFile = "openapi/generated.json")
}
```

For more details about this feature, see [OpenAPI specification generation](openapi-spec-generation.md).


## Shared

### Updated Jetty version

The Jetty server and client engines have been upgraded to use Jetty 12. For most applications, this upgrade is fully
backward-compatible, but client and server code now leverage the updated Jetty APIs internally.

If your project uses Jetty APIs directly, be aware that there are breaking changes. For more details, refer to
[the official Jetty migration guide](https://jetty.org/docs/jetty/12.1/programming-guide/migration/11-to-12.html).

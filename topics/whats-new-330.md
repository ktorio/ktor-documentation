[//]: # (title: What's new in Ktor 3.3.0)

<show-structure for="chapter,procedure" depth="2"/>

## Ktor Server

### Custom fallback for static resources

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

### Updated OkHttp version

In Ktor 3.3.0, the Ktor client's `OkHttp` engine has been upgraded to use OkHttp 5.1.0 (previously 4.12.0). This major
version bump may introduce API changes for projects that interact directly with OkHttp. Such projects should verify
compatibility.

### Unified OkHttp SSE session

The OkHttp engine now uses the standard `DefaultClientSSESession` for Server-Sent Events (SSE), 
replacing the previously introduced `OkHttpSSESession`. 
This change unifies SSE handling across all client engines and addresses the limitations of the OkHttp-specific implementation.

## Shared

### Updated Jetty version

The Jetty server and client engines have been upgraded to use Jetty 12. For most applications, this upgrade is fully
backward-compatible, but client and server code now leverage the updated Jetty APIs internally.

If your project uses Jetty APIs directly, be aware that there are breaking changes. For more details, refer to
[the official Jetty migration guide](https://jetty.org/docs/jetty/12.1/programming-guide/migration/11-to-12.html).

<show-structure for="chapter,procedure" depth="2"/>

# What's new in Ktor 3.2.0

Here are the highlights for this feature release:

* Version Catalog
* Dependency Injection
* First-class HTMX support
* Suspendable module methods

## Ktor Client

### `SaveBodyPlugin` and `HttpRequestBuilder.skipSavingBody()` are deprecated

Prior to Ktor 3.2.0, the `SaveBodyPlugin` was installed by default. It cached the entire response body in memory,
allowing it to be accessed multiple times. To avoid saving response body, the plugin had to be disabled explicitly.

Starting with Ktor 3.2.0, the `SaveBodyPlugin` is deprecated and replaced by a new internal plugin that automatically saves
the response body for all non-streaming requests. This improves resource management and simplifies the HTTP response
lifecycle.

The `HttpRequestBuilder.skipSavingBody()` is also deprecated. If you need to handle a response
without caching the body, use a streaming approach instead.

<compare first-title="Before" second-title="After">

```kotlin
val file = client.get("/some-file") {
    skipSavingBody()
}.bodyAsChannel()
saveFile(file)
```
```kotlin
client.prepareGet("/some-file").execute { response ->
    saveFile(response.bodyAsChannel())
}
```

</compare>

This approach streams the response directly, preventing the body from being saved in memory.

### The `.wrapWithContent()` and `.wrap()` extension functions are deprecated

In Ktor 3.2.0, the [`.wrapWithContent()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.observer/wrap-with-content.html) and [`.wrap()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.observer/wrap.html) extension functions
are deprecated in favor of the new `.replaceResponse()` function.

The `.wrapWithContent()` and `.wrap()` functions replace the original response body with a `ByteReadChannel` that can only be read once.
If the same channel instance is passed directly instead of a function that returns a new one, reading the body multiple times fails.
This can break compatibility between different plugins accessing the response body, because the first plugin to read it consumes the body:

```kotlin
// Replaces the body with a channel decoded once from rawContent
val decodedBody = decode(response.rawContent)
val decodedResponse = call.wrapWithContent(decodedBody).response

// The first call returns the body
decodedResponse.bodyAsText()

// Subsequent calls return an empty string
decodedResponse.bodyAsText() 
```

To avoid this issue, use the `.replaceResponse()` function instead.
It accepts a lambda that returns a new channel on each access, ensuring safe integration with other plugins:

```kotlin
// Replaces the body with a new decoded channel on each access
call.replaceResponse {
    decode(response.rawContent)
}
```

### Access resolved IP address

You can now use the new `resolveAddress()` function on `io.ktor.network.sockets.InetSocketAddress` instances.
This function allows you to obtain the raw resolved IP address of the associated host:

```kotlin
val address = InetSocketAddress("sample-proxy-server", 1080)
val rawAddress = address.resolveAddress()
```

It returns the resolved IP address as a `ByteArray`, or `null` if the address cannot be resolved.
On JS and Wasm platforms, `resolveAddress()` will always return `null`.

## Gradle plugin

### Enabling development mode via the command line

Ktor 3.2.0 simplifies enabling development mode. Previously, enabling development mode required explicit 
configuration in the `application` block. Now, you can use the `ktor.development` property to enable it,
either dynamically or explicitly:

* Dynamically enable development mode based on a project property.
  ```kotlin
    ktor {
        development = project.ext.has("development")
    }
  ```
* Explicitly set development mode to true.

    ```kotlin
    ktor {
        development = true
    }
    ```

By default, the `ktor.development` value is automatically resolved from the Gradle project property or 
the system property `io.ktor.development` if either is defined. This allows you to enable development mode
directly using a Gradle CLI flag:

```bash
./gradlew run -Pio.ktor.development=true
```

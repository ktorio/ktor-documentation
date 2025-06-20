<show-structure for="chapter,procedure" depth="2"/>

# What's new in Ktor 3.2.0

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

## Shared

### HTMX Integration

<primary-label ref="experimental"/>

Ktor 3.2.0 introduces experimental support for [HTMX](https://htmx.org/) a modern JavaScript library that enables dynamic interactions via
HTML attributes like `hx-get` and `hx-swap`. Ktor’s HTMX integration provides:

- HTMX-aware routing for handling HTMX requests based on headers.
- HTML DSL extensions to generate HTMX attributes in Kotlin.
- HTMX header constants and values to eliminate string literals.

Ktor’s HTMX support is available across three experimental modules:

| Module             | Description                                |
|--------------------|--------------------------------------------|
| `ktor-htmx`        | Core definitions and header constants      |
| `ktor-htmx-html`   | Integration with the Kotlin HTML DSL       |
| `ktor-server-htmx` | Routing support for HTMX-specific requests |

All APIs are marked with `@ExperimentalKtorApi` and require opt-in via `@OptIn(ExperimentalKtorApi::class)`.

For more information, see [](htmx-integration.md).

## Infrastructure

### Published version catalog

With this release, you can now use an official
[published version catalog](server-dependencies.topic#using-version-catalog)
to manage all Ktor dependencies from a single source. This eliminates the need to manually declare Ktor versions in
your dependencies.

To add the catalog to your project, configure Gradle’s version catalog in **settings.gradle.kts**, then reference it in
your module’s **build.gradle.kts** file:

<tabs>
<tab title="settings.gradle.kts">

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:%ktor_version%")
        }
    }
}
```

</tab>
<tab title="build.gradle.kts">

```kotlin
dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    // ...
}
```

</tab>
</tabs>
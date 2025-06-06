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

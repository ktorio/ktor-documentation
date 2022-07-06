[//]: # (title: Caching)

<tldr>
<var name="example_name" value="client-caching"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The HttpCache allows you to save previously fetched resources in an in-memory cache.
</link-summary>

The Ktor client provides the [HttpCache](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.cache/-http-cache/index.html) plugin that allows you to save previously fetched resources in an in-memory cache.


## Add dependencies {id="add_dependencies"}
`HttpCache` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.

## Install HttpCache {id="install_plugin"}
To install `HttpCache`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(HttpCache)
}
```

This is enough to enable the client to save previously fetched resources in a cache. For example, if you make two consequent requests to a resource with the configured `Cache-Control` header, the client executes only the first request and skips the second one since data is already saved in a cache.

```kotlin
```
{src="snippets/client-caching/src/main/kotlin/com/example/Application.kt" lines="12-19"}

You can find a runnable example here: [client-caching](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-caching).

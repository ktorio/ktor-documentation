[//]: # (title: Caching)

<tldr>
<var name="example_name" value="client-caching"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The HttpCache allows you to save previously fetched resources in an in-memory or persistent cache.
</link-summary>

The Ktor client provides the [HttpCache](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.cache/-http-cache/index.html) plugin that allows you to save previously fetched resources in an in-memory or persistent cache.


## Add dependencies {id="add_dependencies"}
`HttpCache` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.

## In-memory cache {id="memory_cache"}
To install `HttpCache`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
//...
val client = HttpClient(CIO) {
    install(HttpCache)
}
```

This is enough to enable the client to save previously fetched resources in an in-memory cache.
For example, if you make two consequent [requests](request.md) to a resource with the configured `Cache-Control` header,
the client executes only the first request and skips the second one since data is already saved in a cache.

## Persistent cache {id="persistent_cache"}

Ktor allows you to create a persistent cache by implementing the `CacheStorage` interface.
On JVM, you can use a file storage implementation represented by the `FileStorage` class.

To create a file cache storage, pass the `File` instance to the `FileStorage` constructor.
Then, pass the created storage to the `publicStorage` or `privateStorage` function depending on 
whether this storage is used as a shared or private cache.

```kotlin
```
{src="snippets/client-caching/src/main/kotlin/com/example/Application.kt" include-lines="14-18,20"}

> You can find the full example here: [client-caching](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-caching).

[//]: # (title: Caching)

<primary-label ref="client-plugin"/>

<tldr>
<var name="example_name" value="client-caching"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The HttpCache plugin allows you to save previously fetched resources in an in-memory or persistent cache.
</link-summary>

The Ktor client provides the [`HttpCache`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache/-http-cache/index.html)
plugin for caching previously fetched resources in memory or persistent storage.

## Add dependencies {id="add_dependencies"}

The `HttpCache` plugin is included in the [`ktor-client-core`](client-dependencies.md) artifact and doesn't require any additional
dependencies.

## In-memory cache {id="memory_cache"}

To enable in-memory cache, install `HttpCache` in the [client configuration block](client-create-and-configure.md#configure-client):
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
//...
val client = HttpClient(CIO) {
    install(HttpCache)
}
```

By default, the `HttpCache` plugin stores cached responses in memory.

For example, if you make two consecutive [requests](client-requests.md) to a resource with a configured `Cache-Control` header,
the client can serve the second response from the cache instead of requesting the resource again.

## Persistent cache {id="persistent_cache"}

You can store cached responses persistently by configuring a [`CacheStorage`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache.storage/-cache-storage/index.html)
implementation.

Ktor provides the [`FileStorage()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache.storage/-file-storage.html)
function, which stores cached responses in the file system. `FileStorage()` uses `kotlinx-io` and is available on all
supported platforms.

Create a `Path` for the cache directory and pass it to the `FileStorage()` function.
Then, configure the storage using the `publicStorage()` or `privateStorage()` functions:

```kotlin
```
{src="snippets/client-caching/src/main/kotlin/com/example/Application.kt" include-lines="18-21,23"}

* Use the [`publicStorage()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache/-http-cache/-config/public-storage.html)
  function for responses that can be stored in a shared cache.
* Use the [`privateStorage()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache/-http-cache/-config/private-storage.html)
  function for responses intended for a private cache.

> For the full example, see [client-caching](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-caching).
>
{style="tip"}
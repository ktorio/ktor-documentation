[//]: # (title: Caching)

<microformat>
<var name="example_name" value="client-caching"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The Ktor client provides the `HttpCache` plugin that allows you to save previously fetched resources in an in-memory cache.


## Add dependencies {id="add_dependencies"}
`HttpCache` only requires the [ktor-client-core](client.md#client-dependency) artifact and doesn't need any specific dependencies.

## Install HttpCache {id="install_plugin"}
To install `HttpCache`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(HttpCache)
}
```

This is enough to enable the client to save previously fetched resources in a cache. For example, if you make two consequent requests to a resource with the configured `Cache-Control` header, the client executes only the first request and skips the second one since data is already saved in a cache.

```kotlin
```
{src="snippets/client-caching/src/main/kotlin/com/example/Application.kt" lines="12-19"}

You can find a runnable example here: [client-caching](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-caching).

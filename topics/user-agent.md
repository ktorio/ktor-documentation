[//]: # (title: User agent)

The [UserAgent](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-user-agent/index.html) plugin adds a `User-Agent` header to all [requests](request.md).

## Add dependencies {id="add_dependencies"}

`UserAgent` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.



## Install and configure UserAgent {id="install_plugin"}

To install `UserAgent`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client). Then, use the `agent` property to specify the `User-Agent` value:

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
// ...
val client = HttpClient(CIO) {
    install(UserAgent) {
        agent = "Ktor client"
    }
}
```

Ktor also allows you to add a browser- or curl-like `User-Agent` value using corresponding functions:

```kotlin
val client = HttpClient(CIO) {
    BrowserUserAgent()
    // ... or
    CurlUserAgent()
}

```

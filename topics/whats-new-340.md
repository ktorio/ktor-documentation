[//]: # (title: What's new in Ktor 3.4.0)

<show-structure for="chapter,procedure" depth="2"/>

[//]: # (TODO: Change date)

_[Released: December XX, 2025](releases.md#release-details)_

## Ktor Server

### OAuth fallback for error handling

Ktor 3.4.0 introduces a new `fallback()` function for the [OAuth](server-oauth.md) authentication provider.
The fallback is invoked when the OAuth flow fails with `AuthenticationFailedCause.Error`, such as token exchange
failures, network issues, or response parsing errors.

Previously, you might have used `authenticate(optional = true)` on OAuth-protected routes to bypass OAuth failures.
However, optional authentication only suppresses challenges when no credentials are provided and does not cover actual
OAuth errors.

The new `fallback()` function provides a dedicated mechanism for handling these scenarios. If the fallback does not
handle the call, Ktor returns `401 Unauthorized`.

To configure a fallback, define it inside the `oauth` block:

```kotlin
install(Authentication) {
    oauth("login") {
        client = ...
        urlProvider = ...
        providerLookup = { ... }
        fallback = { cause ->
            if (cause is OAuth2RedirectError) {
                respondRedirect("/login-after-fallback")
            } else {
                respond(HttpStatusCode.Forbidden, cause.message)
            }
        }
    }
}
```

### Zstd compression support

[Ztsd](https://github.com/facebook/zstd) compression is now supported by the [Compression](server-compression.md)
plugin.
`Zstd` is a fast compression algorithm that offers high compression ratios and low compression times, and has a
configurable compression level. To enable it, specify the `zstd {}` block inside the `install(Compression) {}` block 
with the desired configuration:

```kotlin
install(Compression) {
    zstd {
        compressionLevel = 3
        ...
    }
}
```

### SSL trust store settings in a configuration file

Ktor now allows you to configure additional [SSL settings](server-ssl.md#config-file) for the server using the 
application configuration file. You can specify a trust store, its corresponding password, and the list of enabled TLS
protocols directly in your configuration.

You define these settings under the `ktor.security.ssl` section:

```kotlin
// application.conf
ktor {
    security {
        ssl {
            // ...
            trustStore = truststore.jks
            trustStorePassword = foobar
            enabledProtocols = ["TLSv1.2", "TLSv1.3"]
        }
    }
}
```

From the code above:
- `trustStore` – the path to the trust store file containing trusted certificates.
- `trustStorePassword` – password for the trust store.
- `enabledProtocols` – a list of allowed TLS protocols.

### HTML fragments for partial responses

Ktor now provides a new `.respondHtmlFragment()` function for sending partial HTML responses.
This is useful when generating markup that does not require a full `<html>` document, such as dynamic UI updates with
tools like HTMX.

The new API is part of the [HTML DSL](server-html-dsl.md) plugin and allows you to return HTML rooted in any element:

```kotlin
get("/books.html") {
    call.respondHtmlFragment {
        div("books") {
            for (book in library.books()) {
                bookItem()
            }
        }
    }
}
```

### HTTP request lifecycle

The new [`HttpRequestLifecycle` plugin](server-http-request-lifecycle.md) allows you to cancel inflight HTTP requests when the client disconnects.
This is useful when you need to cancel an inflight HTTP request for a long-running or resource-intensive request
when the client disconnects. 

Enable this feature by installing the `HttpRequestLifecycle` plugin and setting `cancelCallOnClose = true`:

```kotlin
install(HttpRequestLifecycle) {
    cancelCallOnClose = true
}

routing {
    get("/long-process") {
        try {
            while (isActive) {
                delay(10_000)
                logger.info("Very important work.")
            }
            call.respond("Completed")
        } catch (e: CancellationException) {
            logger.info("Cleaning up resources.")
        }
    }
}
```

When the client disconnects, the coroutine handling the request is canceled, and structured concurrency handles cleaning
all resources. Any `launch` or `async` coroutines started by the request are also canceled.
This is currently only supported for the `Netty` and `CIO` engine.

### New method to respond with a resource

The new [`call.respondResource()`](server-responses.md#resource) method works in a similar way to [`call.respondFile()`](server-responses.md#file), 
but accepts a resource instead of a file to respond with.

To serve a single resource from the classpath, use `call.respondResource()` and specify the resource path:

```kotlin
routing {
    get("/resource") {
        call.respondResource("public/index.html")
    }
}
```

### API Key authentication

The new [API Key authentication plugin](server-api-key-auth.md) allows you to secure server routes using a shared secret
passed with each request, typically in an HTTP header.

The `apiKey` provider integrates with Ktor’s [Authentication plugin](server-auth.md) and lets you validate incoming API
keys using custom logic, customize the header name, and protect specific routes with standard `authenticate` blocks:

```kotlin
install(Authentication) {
    apiKey("my-api-key") {
        validate { apiKey ->
            if (apiKey == "secret-key") {
                UserIdPrincipal(apiKey)
            } else {
                null
            }
        }
    }
}

routing {
    authenticate {
        get("/") {
            val principal = call.principal<UserIdPrincipal>()!!
            call.respondText("Key: ${principal.key}")
        }
    }
}
```
API Key authentication can be used for service-to-service communication and other scenarios where a lightweight
authentication mechanism is sufficient.

For more details and configuration options, see [](server-api-key-auth.md).

## Core

### Multiple header parsing

The new `Headers.getSplitValues()` function simplifies working with headers that contain multiple values
in a single line.

The `getSplitValues()` function returns all values for the given header and splits them using the specified separator
(`,` by default):

```kotlin
val headers = headers {
    append("X-Multi-Header", "1, 2")
    append("X-Multi-Header", "3")
}

val splitValues = headers.getSplitValues("X-Multi-Header")!!
// ["1", "2", "3"]
```
By default, separators inside double-quoted strings are ignored, but you can change this by setting 
`splitInsideQuotes = true`:

```kotlin
val headers = headers {
    append("X-Multi-Header", """a,"b,c",d""")
}

val forceSplit = headers.getSplitValues("X-Quoted", splitInsideQuotes = true)
// ["a", "\"b", "c\"", "d"]
```

## Ktor Client

### Authentication token cache control

Prior to Ktor 3.4.0, applications using [Basic](client-basic-auth.md) and [Bearer authentication](client-bearer-auth.md)
providers could continue sending outdated tokens or credentials after a user logged out or updated their authentication
data. This happened because each provider internally caches the result of the `loadTokens()` function through
an internal component responsible for storing loaded authentication tokens, and this cache remained active until
manually cleared.

Ktor 3.4.0 introduces new functions and configuration options that give you explicit and convenient control over token
caching behavior.

#### Accessing and clearing authentication tokens

You can now access authentication providers directly from the client and clear their cached tokens when needed.

To clear the token for a specific provider, use the `.clearToken()` function:

```kotlin
val provider = client.authProvider<BearerAuthProvider>()
provider?.clearToken()
```

Retrieve all authentication providers:

```kotlin
val providers = client.authProviders
```

To clear cached tokens from all providers that support token clearing (currently Basic and Bearer), use
the `HttpClient.clearAuthTokens()` function:

```kotlin
 // Clears all cached auth tokens on logout
fun logout() {
    client.clearAuthTokens()
    storage.deleteTokens()
}

// Clears cached auth tokens when credentials are updated
fun updateCredentials(new: Credentials) {
    storage.save(new)
    client.clearAuthTokens()  // Forces reload
}
```

#### Configuring token cache behavior

A new `cacheTokens` configuration option has been added to both Basic and Bearer authentication providers. This allows
you to control whether tokens or credentials should be cached between requests.

For example, you can disable caching when credentials are dynamically provided:

```kotlin
basic {
    cacheTokens = false  // Loads credentials on every request
    credentials {
        getCurrentUserCredentials()
    }
}
```

Disabling caching is especially useful when authentication data changes frequently or must always reflect the most
recent state.

### Duplex streaming for OkHttp

The OkHttp client engine now supports duplex streaming, enabling clients to send request body data and receive response
data simultaneously.

Unlike regular HTTP calls where the request body must be fully sent before the response begins, duplex mode
supports bidirectional streaming, allowing the client to send and receive data concurrently.

Duplex streaming is available for HTTP/2 connections and can be enabled using the new `duplexStreamingEnabled` property
in `OkHttpConfig`:

```kotlin
val client = HttpClient(OkHttp) {
    engine {
        duplexStreamingEnabled = true
        config {
            protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
        }
    }
}
```

### Apache5 connection manager configuration

The Apache5 engine now supports configuring the connection manager directly using the new `configureConnectionManager {}`
function.

This approach is recommended over the previous method using `customizeClient { setConnectionManager(...) }`. Using
`customizeClient` would replace the Ktor-managed connection manager, potentially bypassing engine settings, timeouts,
and other internal configuration.

<compare>

```kotlin
val client = HttpClient(Apache5) {
    engine {
        customizeClient {
            setConnectionManager(
                PoolingAsyncClientConnectionManagerBuilder.create()
                    .setMaxConnTotal(10_000)
                    .setMaxConnPerRoute(1_000)
                    .build()
            )
        }
    }
}
```

```kotlin
val client = HttpClient(Apache5) {
    engine {
        configureConnectionManager {
            setMaxConnTotal(10_000)
            setMaxConnPerRoute(1_000)
        }
    }
}
```

</compare>

The new `configureConnectionManager {}` function keeps Ktor in control while allowing you to adjust parameters such as 
maximum connections per route (`maxConnPerRoute`) and total maximum connections (`maxConnTotal`).

### Dispatcher configuration for native client engines

Native HTTP client engines (`Curl`, `Darwin`, and `WinHttp`) now respect the configured engine dispatcher and use
`Dispatchers.IO` by default.

The `dispatcher` property has always been available on client engine configurations, but native engines previously
ignored it and always used `Dispatchers.Unconfined`. With this change, native engines use the configured dispatcher and
default to `Dispatchers.IO` when none is specified, aligning their behavior with other Ktor client engines.

You can explicitly configure the dispatcher as follows:

```kotlin
val client = HttpClient(Curl) {
    engine {
        dispatcher = Dispatchers.IO
    }
}
```
### HttpStatement execution using the engine dispatcher

The `HttpStatement.execute {}` and `HttpStatement.body {}` blocks now run on the HTTP engine’s dispatcher
instead of the caller’s coroutine context. This prevents accidental blocking when these blocks are invoked from the 
main thread.

Previously, users had to manually switch dispatchers using `withContext` to avoid freezing the UI during I/O operations,
such as writing a streaming response to a file. With this change, Ktor automatically dispatches these blocks to the
engine’s coroutine context:

<compare>

```kotlin
client.prepareGet("https://httpbin.org/bytes/$fileSize").execute { httpResponse ->
    withContext(Dispatchers.IO) {
        val channel: ByteReadChannel = httpResponse.body()
        // Process and write data
    }
}
```

```kotlin
client.prepareGet("https://httpbin.org/bytes/$fileSize").execute { httpResponse ->
    val channel: ByteReadChannel = httpResponse.body()
    // Process and write data
}
```
</compare>

### Plugin and default request configuration replacement

Ktor client configuration now provides more control over replacing existing settings at runtime.

#### Replace plugin configuration

The new `installOrReplace()` function installs a client plugin or replaces its existing configuration if the plugin is
already installed. This is useful when you need to reconfigure a plugin without manually removing it first.

```kotlin
val client = HttpClient {
    installOrReplace(ContentNegotiation) {
        json()
    }
}
```

In the above example, if `ContentNegotiation` is already installed, its configuration is replaced with the new one
provided in the block.

#### Replace default request configuration

The `defaultRequest()` function now accepts an optional `replace` parameter (default is `false`). When set to `true`,
the new configuration replaces any previously defined default request settings instead of merging with them.

```kotlin
val client = HttpClient {
    defaultRequest(replace = true) {
        // ...
    }
}
```

This allows you to explicitly override earlier default request configuration when composing or reusing client setups.

## I/O

### Stream bytes from a `ByteReadChannel` to a `RawSink`

You can now use the new `ByteReadChannel.readTo()` function to read bytes from a channel and write them directly to a
specified `RawSink`. This function simplifies handling large responses or file downloads without intermediate buffers or
manual copying.

The following example downloads a file and writes it to a new local file:

```kotlin
val client = HttpClient(CIO)
val file = File.createTempFile("files", "index")
val stream = file.outputStream().asSink()
val fileSize = 100 * 1024 * 1024

runBlocking {
    client.prepareGet("https://httpbin.org/bytes/$fileSize").execute { httpResponse ->
        val channel: ByteReadChannel = httpResponse.body()
        channel.readTo(stream)
    }
}

println("A file saved to ${file.path}")

```




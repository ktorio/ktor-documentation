[//]: # (title: What's new in Ktor 3.4.0)

<show-structure for="chapter,procedure" depth="2"/>

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

[Ztsd](https://github.com/facebook/zstd) compression is now supported by the [Compression](server-compression.md) plugin.
`Zstd` is a fast compression algorithm that offers high compression ratios and low compression times, and has a
configurable compression level. To enable it, specify the `zstd` block inside the `compression` block with the desired
configuration:

```kotlin
install(Compression) {
    zstd {
        compressionLevel = 3
        ...
    }
}
```

## Core

### Multiple header parsing

A new function, `Headers.getSplitValues()`, has been added to simplify working with headers that contain multiple values
in a single line.

`getSplitValues()` returns all values for the given header and splits them using the specified separator (`,` by default):

```kotlin
val headers = headers {
    append("X-Multi-Header", "1, 2")
    append("X-Multi-Header", "3")
}

val splitValues = headers.getSplitValues("X-Multi-Header")!!
// ["1", "2", "3"]
```
By default, separators inside double-quoted strings are ignored, but this can be changed by setting 
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
`AuthTokenHolder`, and this cache remained active until manually cleared.

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
`HttpClient.clearAuthTokens()`:

```kotlin
 // Clear all cached auth tokens on logout
fun logout() {
    client.clearAuthTokens()
    storage.deleteTokens()
}

// Clear cached auth tokens when credentials are updated
fun updateCredentials(new: Credentials) {
    storage.save(new)
    client.clearAuthTokens()  // Force reload
}
```

#### Configuring token cache behavior

A new `cacheTokens` configuration option has been added to both Basic and Bearer authentication providers. This allows
you to control whether tokens or credentials should be cached between requests.

For example, you can disable caching when credentials are dynamically provided:

```kotlin
basic {
    cacheTokens = false  // Load credentials on every request
    credentials {
        getCurrentUserCredentials()
    }
}
```

Disabling caching is especially useful when authentication data changes frequently or must always reflect the most
recent state.

### Duplex streaming for OkHttp

The OkHttp client engine now supports duplex streaming, enabling clients to send request body data and receive response
data simultaneously. Unlike regular HTTP calls where the request body must be fully sent before the response begins, 
duplex mode supports bidirectional streaming, allowing the client to send and receive data concurrently.

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
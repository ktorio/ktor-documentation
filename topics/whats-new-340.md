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
`splitInsideQuotes = true`.

```kotlin
val headers = headers {
    append("X-Multi-Header", """a,"b,c",d""")
}

val forceSplit = headers.getSplitValues("X-Quoted", splitInsideQuotes = true)
// ["a", "\"b", "c\"", "d"]
```
[//]: # (title: ForwardedHeaderSupport)

<include src="lib.md" include-id="outdated_warning"/>

This feature allows you to handle reverse proxy headers to get information about the original
request when it's behind a proxy.

* `ForwardedHeaderSupport` handles the standard `Forwarded` header ([RFC 7239](https://tools.ietf.org/html/rfc7239))
* `XForwardedHeaderSupport` handles the non-standard (but standard de-facto) `X-Forwarded-Host`/`X-Forwarded-Server`, `X-Forwarded-For`, `X-Forwarded-By`, `X-Forwarded-Proto`/`X-Forwarded-Protocol` and `X-Forwarded-SSL`/`Front-End-Https`

>Only install these features if you have a reverse proxy supporting these headers serving your requests.
>In other cases, a client will be able to manipulate these headers.
>
{type="note"}



## Usage

These features don't require any special configuration.
You can install any of the two depending on your reverse proxy,
but since the standard is the `Forwarded` header, you should
favor it whenever possible.

```kotlin
install(ForwardedHeaderSupport)
```

or 

```kotlin
install(XForwardedHeaderSupport)
```

## Request information

You can see all the [available request properties](/servers/calls/requests.html) on the Requests page.

### The proxy request information

You can read the raw or local request information, read from the received normal
headers and socket properties, that correspond to the proxy request
using the `request.local` property:

```kotlin
val scheme = request.local.scheme
val version = request.local.version
val port = request.local.port
val host = request.local.host
val uri = request.local.uri
val method = request.local.method
val remoteHost = request.local.remoteHost
```

### The original request information

You can read the original request information, read from the `Forwarded`
or `X-Forwarded-*` headers with fallback to the raw headers,
that corresponds to original client request using the `request.origin` property:

```kotlin
val scheme = request.origin.scheme // Determined from X-Forwarded-Proto / X-Forwarded-Protocol / X-Forwarded-SSL
val version = request.origin.version
val port = request.origin.port // Determined from X-Forwarded-Host / X-Forwarded-Server
val host = request.origin.host // Determined from X-Forwarded-Host / X-Forwarded-Server
val uri = request.origin.uri
val method = request.origin.method
val remoteHost = request.origin.remoteHost // Determined from X-Forwarded-For
```

In the cases where you need the X-Forwarded-By (the interface used for the socket), you can access the raw X-Forwarded properties with:

```kotlin
val forwardedValues: List<ForwardedHeaderSupport.ForwardedHeaderValue> = call.attributes[ForwardedHeaderSupport.ForwardedParsedKey]
```

```kotlin
data class ForwardedHeaderValue(val host: String?, val by: String?, val forParam: String?, val proto: String?, val others: Map<String, String>)
```

## Header description

The standard `Forwarded` header looks like this: 

```
Forwarded: by=<identifier>; for=<identifier>; host=<host>; proto=<http|https>
```

* `by` - The interface where the request came in to the proxy server.
* `for` - The client that initiated the request and subsequent proxies in a chain of proxies.
* `host` - The Host request header field as received by the proxy.
* `proto` - Indicates which protocol was used to make the request (typically "http" or "https").

>You can read more about [Forwarded](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded) in the MDN documentation.
>
{type="note"}
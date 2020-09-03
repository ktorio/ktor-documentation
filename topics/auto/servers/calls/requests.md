[//]: # (title: Requests)
[//]: # (caption: Handling HTTP Requests)
[//]: # (category: servers)
[//]: # (permalink: /servers/calls/requests.html)
[//]: # (keywords: multipart receiving)
[//]: # (redirect_from: redirect_from)
[//]: # (- /servers/requests.html: - /servers/requests.html)
[//]: # (ktor_version_review: 1.0.0)

When handling routes, or directly intercepting the pipeline, you
get a context with an [ApplicationCall](/servers/calls.html).
That `call` contains a property called `request` that includes information about the request.

Also, the call itself has some useful convenience properties and methods that rely on the request.





## Introduction
{id="introduction"}

When using the [Routing](/servers/features/routing.html) feature, or when intercepting requests, you can access
the `call` property inside handlers. That call includes a `request` property with relevant information about the request:

```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    } 
}

intercept(ApplicationCallPipeline.Call) { 
    if (call.request.uri == "/") {
        call.respondText("Test String")
    }
}
```

## Request information
{id="info"}

As part of the `request`, you can get access to its internal context:

```kotlin
val call: ApplicationCall = request.call
val pipeline: ApplicationReceivePipeline = request.pipeline
```

### URL, method, scheme, protocol, host, path, httpVersion, remoteHost, clientIp
{id=" info-url"}

```kotlin
val version: String = request.httpVersion // "HTTP/1.1"
val httpMethod: HttpMethod = request.httpMethod // GET, POST... 
val uri: String = request.uri // Short cut for `origin.uri`
val scheme: String = request.origin.scheme // "http" or "https"
val host: String? = request.host() // The host part without the port 
val port: Int = request.port() // Port of request
val path: String = request.path() // The uri without the query string
val document: String = request.document() // The last component after '/' of the uri
val remoteHost: String = request.origin.remoteHost // The IP address of the client doing the request
```

### Reverse proxy support: `origin` and `local`
{id="info-origin-local"}

When behind a reverse-proxy (for example an nginx or a load balancer), the received request is not performed by the end-user, but that reverse proxy.
That means that the client IP address of the connection would be the one of the proxy instead of the client.
Also the reverse proxy might be serving via HTTPS and requesting to your server via HTTP.
Popular reverse proxies send `X-Forwarded-` headers to be able to access this information. 

>Note that for this to work when under a reverse-proxy you have to install the [`XForwardedHeaderSupport` feature](/servers/features/forward-headers.html).
>
{type="note"}

As part of the request object, there are two properties `local` and `origin` that allows to get information of the original request
or the local/proxied one. 

```kotlin
val local : RequestConnectionPoint = request.local // Local information 
val origin: RequestConnectionPoint = request.origin // Local / Origin if XForwardedHeaderSupport feature is installed.
```

The local/origin information you can get:

```kotlin
interface RequestConnectionPoint {
    val scheme: String // "http" or "https": The provided protocol (local) or `X-Forwarded-Proto`
    val version: String // "HTTP/1.1"
    val port: Int
    val host: String // The provided host (local) or `X-Forwarded-Host`
    val uri: String
    val method: HttpMethod
    val remoteHost: String // The client IP (the direct ip for `local`, or the redirected one `X-Forwarded-For`)
}
```

## GET / Query parameters
{id="get"}

If you need to access the query parameters `?param1=value&param2=value` as a collection,
you can use `queryParameters`. It implements the `StringValues` interface where
each key can have a list of Strings associated with it.

```kotlin
val queryParameters: Parameters = request.queryParameters
val param1: String? = request.queryParameters["param1"] // To access a single parameter (first one if repeated)
val repeatedParam: List<String>? = request.queryParameters.getAll("repeatedParam") // Multiple values
```

You can also access the raw `queryString` (`param1=value&param2=value`):

```kotlin
val queryString: String = request.queryString()
```

## POST, PUT and PATCH

`POST`, `PUT` and `PATCH` requests has an associated request body (the payload).
That payload is usually encoded.

All the receive methods consume the whole payload sent by the client so an attempt to receive a request body twice
will lead to `RequestAlreadyConsumedException` error unless you have [DoubleReceive](/servers/features/double-receive.html) feature installed.
{ .note #receiving-several-times}

### Raw payload
{id="payload-data"}

To access the raw bits of the payload, you can use `receiveChannel`, but it is
directly part of the `call` instead of `call.request`:

```kotlin
val channel: ByteReadChannel = call.receiveChannel()
```

And it provide some convenience methods for common types:

```kotlin
val channel: ByteReadChannel = call.receiveChannel()
val text: String = call.receiveText()
val inputStream: InputStream = call.receiveStream() // NOTE: InputStream is synchronous and blocks the thread
val multipart: MultiPartData = call.receiveMultipart()
```

All those receive* methods are aliases of `call.receive<T>` with the specified type.
The types `ByteReadChannel`, `ByteArray`, `InputStream`, `MultiPartData`, `String` and `Parameters` are handled by
`ApplicationReceivePipeline.installDefaultTransformations` that is installed by default.

### Form Parameters (urlencoded or multipart)
{id="post"}

To parse a form urlencoded or with multipart, you can use `receiveParameters` or `receive<Parameters>`:

```kotlin
val postParameters: Parameters = call.receiveParameters()
```

### Receive Typed Objects, Content-Type and JSON
{id="typed-objects"}

The call also supports receiving generic objects:

```kotlin
val obj: T = call.receive<T>()
val obj: T? = call.receiveOrNull<T>()
```

In order to receive custom objects from the payload,
you have to use the `ContentNegotiation` feature.
This is useful for example to receive and send JSON payloads in REST APIs.  

```kotlin
install(ContentNegotiation) {
    gson {
        setDateFormat(DateFormat.LONG)
        setPrettyPrinting()
    }
}
```

If you configure the ContentNegotiation to use gson,
you will need to include the `ktor-gson` artifact:

```kotlin
compile("io.ktor:ktor-gson:$ktor_version")
```

Then you can, as an example, do:

```kotlin
data class HelloWorld(val hello: String)

routing {
    post("/route") {
        val helloWorld = call.receive<HelloWorld>()
    }
}
```

Remember that your classes must be defined top level (outside of any other class or function) to be recognized by Gson. 
{ .note #receiving-gson-top-level}

### Multipart, Files and Uploads
{id="post-files"}

Check the [uploads](/servers/uploads.html) section.

### Custom receive transformers
{id="custom-receive-transformers"}

You can create custom transformers by calling
`application.receivePipeline.intercept(ApplicationReceivePipeline.Transform) { query ->`
and then calling `proceedWith(ApplicationReceiveRequest(query.type, transformed))` as does the [ContentNegotiation feature](/servers/features/content-negotiation.html).

## Cookies
{id="cookies"}

There is a `cookies` property to access the `Cookie` headers sent by the client,
just as if it was a collection:

```kotlin
val cookies: RequestCookies = request.cookies
val mycookie: String? = request.cookies["mycookie"]
```

To handle sessions using cookies, have a look to the [Sessions](/servers/features/sessions.html) feature.

## Headers
{id="headers"}

To access the headers the request objects has a `headers: Headers` property.
It implements the `StringValues` interface where each key can have a list of Strings associated with it.

```kotlin
val headers: Headers = request.headers
val header: String? = request.header("HeaderName") // To access a single header (first one if repeated)
val repeatedHeader: List<String>? = request.headers.getAll("HeaderName") // Multiple values
```

And several convenience methods to access some common headers:

```kotlin
val contentType: ContentType = request.contentType() // Parsed Content-Tpe 
val contentCharset: Charset? = request.contentCharset() // Content-Type JVM charset
val authorization: String? = request.authorization() // Authorization header
val location: String? = request.location() // Location header
val accept: String? = request.accept() // Accept header
val acceptItems: List<HeaderValue> = request.acceptItems() // Parsed items of Accept header
val acceptEncoding: String? = request.acceptEncoding() // Accept-Encoding header
val acceptEncodingItems: List<HeaderValue> = request.acceptEncodingItems() // Parsed Accept-Encoding items 
val acceptLanguage: String? = request.acceptLanguage() // Accept-Language header
val acceptLanguageItems: List<HeaderValue> = request.acceptLanguageItems() // Parsed Accept-Language items
val acceptCharset: String? = request.acceptCharset() // Accept-Charset header
val acceptCharsetItems: List<HeaderValue> = request.acceptCharsetItems() // Parsed Accept-Charset items
val userAgent: String? = request.userAgent() // User-Agent header
val cacheControl: String? = request.cacheControl() // Cache-Control header
val ranges: RangesSpecifier? = request.ranges() // Parsed Ranges header

val isChunked: Boolean = request.isChunked() // Transfer-Encoding: chunked
val isMultipart: Boolean = request.isMultipart() // Content-Type matches Multipart
```
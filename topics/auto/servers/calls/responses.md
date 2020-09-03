[//]: # (title: Responses)
[//]: # (caption: Generating HTTP Responses)
[//]: # (category: servers)
[//]: # (permalink: /servers/calls/responses.html)
[//]: # (keywords: Redirections Location header permanent redirect temporal redirect pushing http2 respondFile respondBytes respondText respond response downloads generating response sending response)
[//]: # (redirect_from: redirect_from)
[//]: # (- /servers/responses.html: - /servers/responses.html)
[//]: # (ktor_version_review: 1.0.0)

When handling routes, or directly intercepting the pipeline, you
get a context with an [ApplicationCall](/servers/calls.html).
That `call` contains a property called `response` that allows you to emit the response.

Also, the call itself has some useful convenience properties and methods 
that interact with the response.

**Table of contents:**

* TOC

## Context
{ #context}

When using the [Routing](/servers/features/routing.html) feature, you can access
the `call` property inside route handlers:

```kotlin
routing {
    get("/") {
        call.respondText("Request uri: ${call.request.uri}")
    } 
}
```

When intercepting requests, the lambda handler in `intercept` has the `call` property available too:

```kotlin
intercept(ApplicationCallPipeline.Call) { 
    if (call.request.uri == "/") {
        call.respondText("Test String")
    }
}
```

## Controlling the HTTP headers and the status
{ #properties}

You can control how the response is generated, the HTTP status, the headers, cookies, and the payload.

Remember that since HTTP requests an responses are non-seekable streams,
once you start emitting the response payload/content, the status and the headers are emitted,
and you won't be able to modify either the status or the headers/cookies.
{ .note #headers-already-sent } 

As part of the `response`, you can get access to its internal context:

* `val call: ApplicationCall = response.call`
* `val pipeline: ApplicationSendPipeline = response.pipeline`

Headers:

* `val headers: ResponseHeaders = response.headers`

Convenience `cookies` instance to set `Set-Cookie` headers:

* `val cookies: ResponseCookies = response.cookies`

Getting and changing the HTTP Status:

* `response.status(HttpStatusCode.OK)` - Sets the HttpStatusCode to a predefined standard one
* `response.status(HttpStatusCode(418, "I'm a tea pot"))` - Sets the HttpStatusCode to a custom status code
* `val status: HttpStatusCode? = response.status()` - Gets the currently set HttpStatusCode if set

* `response.contentType(ContentType.Text.Plain.withCharset(Charsets.UTF_8))` - Typed way for setting the Content-Type (for `ContentType.Application.Json` the default charset is UTF_8 without making it explicit)
* `response.contentType("application/json; charset=UTF-8")` - Untyped way for setting the Content-Type header

Custom headers:

* `response.header("X-My-Header", "my value")` - Appends a custom header
* `response.header("X-My-Times", 1000)` - Appends a custom header
* `response.header("X-My-Times", 1000L)` - Appends a custom header
* `response.header("X-My-Date", Instant.EPOCH)` - Appends a custom header

Convenience methods to set headers usually set by the infrastructure:

* `response.etag("33a64df551425fcc55e4d42a148795d9f25f89d4")` - Sets the `ETag` used for caching
* `response.lastModified(ZonedDateTime.now())` - Sets the `Last-Modified` header
* `response.contentLength(1024L)` - Sets the `Content-Length`. This is generally automatically set when sending the payload
* `response.cacheControl(CacheControl.NoCache(CacheControl.Visibility.Private))` - Sets the Cache-Control header in a typed way
* `response.expires(LocalDateTime.now())` - Sets the `Expires` header
* `response.contentRange(1024L until 2048L, 4096L)` - Sets the `Content-Range` header (check the [PartialContent](/servers/features/partial-content.html) feature) 

## HTTP/2 pushing and HTTP/1 `Link `header
{ #pushing}

The `call` supports pushing.

* In HTTP/2 it uses the push feature
* In HTTP/1.2 it adds the `Link` header as a hint

```kotlin
routing {
    get("/") {
        call.push("/style.css")
    }
}
```

Pushing reduces the time between the request and the display of the page.
But beware that sending content beforehand might send content that is already cached by the client.
{ .note.performance }

## Redirections

You can easily generate redirection responses with the `respondRedirect` method,
to send `301 Moved Permanently` or `302 Found` redirects, with a `Location` header.

```kotlin
call.respondRedirect("/moved/here", permanent = true)
```

Remember that once this function is executed, the rest of the function is still executed. Therefore, if you have it in a guard
clause, you should return from the function to avoid continuing with the rest of the handler.
If you want to make redirections that stop the control flow by throwing an exception, check out this [sample from status pages](/servers/features/status-pages.html#redirect).
{ .note}

## Sending response content

Sending generic content (compatible with [Content negotiation](#content-negotiation)):
{ #call-respond}

* `call.respond(MyDataClass("hello", "world"))` - Check the [Content Negotiation](#content-negotiation) section
* `call.respond(HttpStatusCode.NotFound, MyDataClass("hello", "world"))` - Specifies a status code, and sends a payload in a single call. Check [StatusPages](/servers/features/status-pages.html)

Sending plain text:

* `call.respondText("text")` - Just a string with the body
* `call.respondText("p { background: red; }", contentType = ContentType.Text.CSS, status = HttpStatusCode.OK) { ... }` - Sending a text specifying the ContentType, the HTTP Status and configuring the [OutgoingContent](#outgoing-content)
* `call.respondText { "string" }` - Responding a string with a suspend provider
* `call.respondText(contentType = ..., status = ...) { "string" }` - Responding a string with a suspend provider
* `call.respond(TextContent("{}", ContentType.Application.Json))` - Responding a string without adding a charset to the `Content-Type` 

Sending byte arrays:

* `call.respondBytes(byteArrayOf(1, 2, 3))` - A ByteArray with a binary body

Sending files:

* `call.respondFile(File("/path/to/file"))` - Sends a file
* `call.respondFile(File("basedir"), "filename") { ... }` - Send a file and configures the [OutgoingContent](#outgoing-content)

Sending URL-encoded forms (`application/x-www-form-urlencoded`):

* Use `Parameters.formUrlEncode`. Check the [Utilities page](/advanced/utilities.html) for more information about this.

When sending files based on the request parameters,
be especially careful validating and limiting the input.
{ .note.security #validate-respond-file-parameters }

Sending chunked content using a Writer:

* `call.respondWrite { write("hello"); write("world") }` - Sends text using a writer. This is used with the [HTML DSL](#html-dsl)
* `call.respondWrite(contentType = ..., status = ...) { write("hello"); write("world") }` - Sends text using a writer and specifies a contentType and a status

Sending arbitrary data in chunks using `WriteChannelContent`:

```kotlin
call.respond(object : OutgoingContent.WriteChannelContent() {
    override val contentType = ContentType.Application.OctetStream
    override suspend fun writeTo(channel: ByteWriteChannel) {
        channel.writeFully(byteArray1)
        channel.writeFully(byteArray2)
        // ...
    }
})
```

To specify a default content type for the request:

* `call.defaultTextContentType(contentType: ContentType?): ContentType`

The OutgoingContent interface for configuring responses:
{ #outgoing-content}

```kotlin
class OutgoingContent {
    val contentType: ContentType? get() = null // * Specifies [ContentType] for this resource.
    val contentLength: Long? get() = null // Specifies content length in bytes for this resource. - If null, the resources will be sent as `Transfer-Encoding: chunked` 
    val status: HttpStatusCode? // Status code to set when sending this content
    val headers: Headers // Headers to set when sending this content
    fun <T : Any> getProperty(key: AttributeKey<T>): T? = extensionProperties?.getOrNull(key) // Gets an extension property for this content
    fun <T : Any> setProperty(key: AttributeKey<T>, value: T?) // Sets an extension property for this content
}
```

## Making files downloadable
{ #content-disposition }

You can make files "downloadable", by adding the [`Content-Disposition` header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition).

In an untyped way, you can do something like:

```kotlin
call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"myfilename.bin\"")
```

But Ktor also provides a typed way with proper escaping to generate this header:

```kotlin
call.response.header(HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "myfilename.bin").toString())
```

## Content negotiation
{ #content-negotiation}

When configuring plugins for content negotiation, the pipeline may accept additional
types for the [`call.respond`](#call-respond) methods.

### Sending HTML with the DSL
{ #html-dsl}

Ktor includes an optional [feature to send HTML content using a DSL](/servers/features/templates/html-dsl.html).

### Sending HTML with FreeMarker
{ #html-freemarker}

Ktor includes an optional [feature to send HTML content using FreeMarker](/servers/features/templates/freemarker.html).

### Sending JSON with data classes
{ #json}

Ktor includes an optional [feature to send JSON content using Content negotiation](/servers/features/content-negotiation/gson.html).
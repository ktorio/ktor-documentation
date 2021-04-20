[//]: # (title: Receiving responses)

<include src="lib.md" include-id="outdated_warning"/>

## Receiving the body of a response {id="receive"}

By default you can use `HttpResponse` or `String` as possible types for typed
HttpClient requests. So for example:

```kotlin
val htmlContent = client.get<String>("https://en.wikipedia.org/wiki/Main_Page")
val response = client.get<HttpResponse>("https://en.wikipedia.org/wiki/Main_Page")
```

If *JsonFeature* is configured, and the server returns the header `Content-Type: application/json`,
you can also specify a class for deserializing it.

```kotlin
val helloWorld = client.get<HelloWorld>("http://127.0.0.1:8080/")
```

### The `HttpResponse` class {id="HttpResponse"}

`HttpResponse` API reference is listed [here](https://api.ktor.io/%ktor_version%/io.ktor.client.response/-http-response/).

From an `HttpResponse`, you can get the response content easily:

* `val readChannel: ByteReadChannel = response.content`
* `val bytes: ByteArray = response.readBytes()`
* `val text: String = response.readText()`
* `val readChannel = response.receive<ByteReadChannel>()`
* `val multiPart = response.receive<MultiPartData>()`
* `val inputStream = response.receive<InputStream>()` *Remember that InputStream API is synchronous!*
* `response.discardRemaining()`

You can also get the additional response information such as its status, headers, internal state, etc.:

### *Basic*

* `val status: HttpStatusCode = response.status`
* `val headers: Headers = response.headers`

### *Advanced*

* `val call: HttpClientCall = response.call`
* `val version: HttpProtocolVersion = response.version`
* `val requestTime: Date = response.requestTime`
* `val responseTime: Date = response.responseTime`
* `val executionContext: Job = response.executionContext`

### *Extensions for headers*

* `val contentType: ContentType? = response.contentType()`
* `val charset: Charset? = response.charset()`
* `val lastModified: Date? = response.lastModified()`
* `val etag: String? = response.etag()`
* `val expires: Date? = response.expires()`
* `val vary: List<String>? = response.vary()`
* `val contentLength: Int? = response.contentLength()`
* `val setCookie: List<Cookie> = response.setCookie()`
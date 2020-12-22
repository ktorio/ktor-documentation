[//]: # (title: Request)

<include src="lib.md" include-id="outdated_warning"/>

## Making request

After client configuration we're ready to perform our first request.
Most of the simple requests are made with pattern

```kotlin
val response = client.'http-method'<'ResponseType'>("url-string")
```

or even simpler form(due to kotlin generic type inference):

```kotlin
val response: ResponseType = client.'http-method'("url-string")
```

For example to perform a `GET` request fully reading a `String`:

```kotlin
val htmlContent = client.get<String>("https://en.wikipedia.org/wiki/Main_Page")
// same as
val content: String = client.get("https://en.wikipedia.org/wiki/Main_Page")
```

And in the case you are interested in the raw bits, you can read a `ByteArray`:

```kotlin
val channel: ByteArray = client.get("https://en.wikipedia.org/wiki/Main_Page")
```

Or get full [HttpResponse](https://api.ktor.io/%ktor_version%/io.ktor.client.statement/-http-response/index.html):

```kotlin
val response: HttpResponse = client.get("https://en.wikipedia.org/wiki/Main_Page")
```

The [HttpResponse](https://api.ktor.io/%ktor_version%/io.ktor.client.statement/-http-response/index.html) is downloaded in memory by default. To learn how to download response partially or work with a stream data consult with the [Streaming](streaming.md) section.

And even your data class using [Json](json-feature.md) feature:

```kotlin
@Serializable
data class User(val id: Int)

val response: User = client.get("https://myapi.com/user?id=1")
```

Please note that some of response types are `Closeable` and can hold resources.

## Customizing requests

We cannot live only on *get* requests, Ktor allows you to build complex requests with any of the HTTP verbs, with the flexibility to process responses in many ways.

### Default http methods

{id="shortcut-methods"}

Similar to `request`, there are several extension methods to perform requests
with the most common HTTP verbs: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD` and `OPTIONS`.

```kotlin
val text = client.post<String>("http://127.0.0.1:8080/")
```

When calling request methods, you can provide a lambda to build the request
parameters like the URL, the HTTP method(verb), the body, or the headers:

```kotlin
val text = client.post<String>("http://127.0.0.1:8080/") {
    header("Hello", "World")
}
```

The [HttpRequestBuilder](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/) looks like this:

```kotlin
class HttpRequestBuilder : HttpMessageBuilder {
    var method: HttpMethod

    val url: URLBuilder
    fun url(block: URLBuilder.(URLBuilder) -> Unit)

    val headers: HeadersBuilder
    fun header(key: String, value: String)
    fun headers(block: HeadersBuilder.() -> Unit)

    var body: Any = EmptyContent

    val executionContext: CompletableDeferred<Unit>
    fun setAttributes(block: Attributes.() -> Unit)
    fun takeFrom(builder: HttpRequestBuilder): HttpRequestBuilder
}
```

The `HttpClient` class only offers some basic functionality, and all the methods for building requests are exposed as extensions.\\
You can check the standard available [HttpClient build extension methods](https://api.ktor.io/%ktor_version%/io.ktor.client.request/).

### Customize method

In addition to call, there is a `request` method for performing a typed request,
[receiving a specific type](response.md#receive) like String, HttpResponse, or an arbitrary class.
You have to specify the URL and the method when building the request.

```kotlin
val call = client.request<String> {
    url("http://127.0.0.1:8080/")
    method = HttpMethod.Get
}
```

### Posting forms

{id="submit-form"}

There are a couple of convenience extension methods for submitting form information.
The detailed reference is listed [here](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/).

The `submitForm` method:

```kotlin
client.submitForm(
    formParameters: Parameters = Parameters.Empty,
    encodeInQuery: Boolean = false,
    block: HttpRequestBuilder.() -> Unit = {}
)
```

It allows requesting with the `Parameters` encoded in the query string(`GET` by default) or requesting with the `Parameters` encoded as multipart(`POST` by default) depending on the `encodeInQuery` parameter.

The `submitFormWithBinaryData` method:

```kotlin
client.submitFormWithBinaryData(
    formData: List<PartData>,
    block: HttpRequestBuilder.() -> Unit = {}
): T
```

It allows to generate a multipart POST request from a list of `PartData`.
`PartData` can be `PartData.FormItem`, `PartData.BinaryItem` or `PartData.FileItem`.

To build a list of `PartData`, you can use the `formData` builder:

```kotlin
val data: List<PartData> = formData {
    // Can append: String, Number, ByteArray and Input.
    append("hello", "world")
    append("number", 10)
    append("ba", byteArrayOf(1, 2, 3, 4))
    appendInput("input", size = knownSize.orNull()) { openInputStream().asInput() }
    // Allow to set headers to the part:
    append("hello", "world", headersOf("X-My-Header" to "MyValue"))
}
```

### Specifying custom headers

{id="custom-headers"}

When building requests with `HttpRequestBuilder`, you can set custom headers.
There is a final property `val headers: HeadersBuilder` that inherits from `StringValuesBuilder`.
You can add or remove headers using it, or with the `header` convenience methods.

```kotlin
// this : HttpMessageBuilder

// Convenience method to add a header
header("My-Custom-Header", "HeaderValue")

// Calls methods from the headers: HeadersBuilder to manipulate the headers
headers.clear()
headers.append("My-Custom-Header", "HeaderValue")
headers.appendAll("My-Custom-Header", listOf("HeaderValue1", "HeaderValue2"))
headers.remove("My-Custom-Header")

// Applies the headers with the `headers` convenience method
headers { // this: HeadersBuilder
    clear()
    append("My-Custom-Header", "HeaderValue")
    appendAll("My-Custom-Header", listOf("HeaderValue1", "HeaderValue2"))
    remove("My-Custom-Header")
}
```

Complete `HeadersBuilder` API is listed [here](https://api.ktor.io/%ktor_version%/io.ktor.http/-headers-builder/).

## Specifying a body for requests

For `POST` and `PUT` requests, you can set the `body` property:

```kotlin
client.post<Unit> {
    url("http://127.0.0.1:8080/")
    body = // ...
}
```

The `HttpRequestBuilder.body` property can be a subtype of `OutgoingContent` as well as a `String` instance:

* `body = "HELLO WORLD!"`
* `body = TextContent("HELLO WORLD!", ContentType.Text.Plain)`
* `body = ByteArrayContent("HELLO WORLD!".toByteArray(Charsets.UTF_8))`
* `body = LocalFileContent(File("build.gradle"))`
* `body = JarFileContent(File("myjar.jar"), "test.txt", ContentType.fromFileExtension("txt").first())`
* `body = URIFileContent("https://en.wikipedia.org/wiki/Main_Page")`

If you install the [JsonFeature](json-feature.md), and set the content type to `application/json`
you can use arbitrary instances as the `body`, and they will be serialized as JSON:

```kotlin
data class HelloWorld(val hello: String)

val client = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = GsonSerializer {
            // Configurable .GsonBuilder
            serializeNulls()
            disableHtmlEscaping()
        }
    }
}

client.post<Unit> {
    url("http://127.0.0.1:8080/")
    body = HelloWorld(hello = "world")
}
```

Alternatively (using the integrated `JsonSerializer`):

```kotlin
val json = io.ktor.client.features.json.defaultSerializer()
client.post<Unit>() {
    url("http://127.0.0.1:8080/")
    body = json.write(HelloWorld(hello = "world")) // Generates an OutgoingContent
}
```

Or using Jackson (JVM only):

```kotlin
val json = jacksonObjectMapper()
client.post<Unit> {
    url("http://127.0.0.1:8080/")
    body = TextContent(json.writeValueAsString(userData), contentType = ContentType.Application.Json)
}
```

>Remember that your classes must be *top-level* to be recognized by `Gson`. \\
>If you try to send a class that is inside a function, the feature will send a *null*.
>
{type="note"}

## Uploading multipart/form-data
{id="multipart-form-data"}

Ktor HTTP Client has support for making MultiPart requests.
The idea is to use the `MultiPartFormDataContent(parts: List<PartData>)` as `OutgoingContent` for the body of the request.

The easiest way is to use the [`submitFormWithBinaryData` method](#submit-form).

Alternatively, you can set the body directly:

```kotlin
val request = client.request {
    method = HttpMethod.Post
    body = MultiPartFormDataContent(formData {
        append("key", "value")
    })
}
```

[//]: # (title: Making requests)

<show-structure for="chapter" depth="2"/>

[percent_encoding]: https://en.wikipedia.org/wiki/Percent-encoding

<tldr>
<var name="example_name" value="client-configure-request"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to make requests and specify various request parameters: a request URL, an HTTP method, headers, and the body
of a request.
</link-summary>

After [configuring the client](client-create-and-configure.md), you can start making HTTP requests. The primary way to
do this is by using the
[`.request()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/request.html)
function that accepts a URL as a parameter. Inside this function, you can configure various request parameters:

* Specify an HTTP method, such as `GET`, `POST`, `PUT`, `DELETE`, `HEAD`, `OPTIONS`, or `PATCH`.
* Configure a URL as a string or configure its components (such as domain,path, and query parameters) separately.
* Use a Unix domain socket.
* Add headers and cookies.
* Include a request body – for example, plain text, a data object, or form parameters.

These parameters are exposed by the
[`HttpRequestBuilder`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html)
class.

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.request("https://ktor.io/") {
  // Configure request parameters exposed by [[[HttpRequestBuilder|https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html]]]
}
```

{interpolate-variables="true" disable-links="false"}

The `.request()` function returns a response as an `HttpResponse` object. `HttpResponse` exposes the API
required to get a response body in various formats – such as a string, a JSON object, and more – as well as retrieving
response parameters, such as a status code, content type, and headers. For more information,
see [](client-responses.md).

> `.request()` is a suspending function, meaning it must be called from within a coroutine or another suspending
> function. To learn more about suspending functions, see
> [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).

### Specify an HTTP method {id="http-method"}

When calling the `.request()` function, you can specify the desired HTTP method using the `method` property:

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.request("https://ktor.io/") {
    method = HttpMethod.Get
}
```

In addition to `.request()`, `HttpClient` provides specific functions for basic HTTP methods, such as
[`.get()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/get.html),
[`.post()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/post.html), and
[`.put()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/put.html).
The example above can be simplified using the `.get()` function:

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}

In both examples, a request URL is specified as a string. You can also configure URL components separately using
[`HttpRequestBuilder`](#url).

## Specify a request URL {id="url"}

The Ktor client allows you to configure a request URL in multiple ways:

### Pass the entire URL string

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}

### Configure URL components separately

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="22-28"}

In this case, the `url` parameter provided by `HttpRequestBuilder` is used. It accepts an instance of
[`URLBuilder`](https://api.ktor.io/ktor-http/io.ktor.http/-u-r-l-builder/index.html), offering more flexibility for
building complex URLs.

> To configure a base URL for all requests, use the [`DefaultRequest`](client-default-request.md#url) plugin.

### Path segments {id="path_segments"}

In the previous example, the entire URL path was specified using the `URLBuilder.path` property.
Alternatively, you can pass individual path segments using the `appendPathSegments()` function.

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="29-33"}

By default, `appendPathSegments` [encodes][percent_encoding] path segments.
To disable encoding, use `appendEncodedPathSegments()` instead.

### Query parameters {id="query_parameters"}

To add <emphasis tooltip="query_string">query string</emphasis> parameters, use the `URLBuilder.parameters` property:

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="34-38"}

By default, `parameters` [encodes][percent_encoding] query parameters.
To disable encoding, use `encodedParameters()` instead.

> The `trailingQuery` property can be used to keep the `?` character even if there are no query parameters.

### URL fragment {id="url-fragment"}

A hash mark `#` introduces the optional fragment near the end of the URL.
You can configure a URL fragment using the `fragment` property.

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="39-43"}

By default, `fragment` [encodes][percent_encoding] a URL fragment.
To disable encoding, use `encodedFragment()` instead.

## Specify a Unix domain socket

> Unix domain sockets are supported only in the CIO engine.
> To use a Unix socket with a Ktor server, [configure the server](server-configuration-code.topic#cio-code) accordingly.
>
{style="note"}

To send a request to a server listening to a Unix domain socket, call the `unixSocket()` function
when using a CIO client:

```kotlin
val client = HttpClient(CIO)

val response: HttpResponse = client.get("/") {
    unixSocket("/tmp/test-unix-socket-ktor.sock")
}
```

You can also configure a Unix domain socket as a part of a
[default request](client-default-request.md#unix-domain-sockets).

## Set request parameters {id="parameters"}

You can specify various request parameters, including an HTTP method, headers, and cookies. If you need to configure
default parameters for all requests of a specific client, use the [`DefaultRequest`](client-default-request.md) plugin.

### Headers {id="headers"}

You can add headers to a request in several ways:

#### Add multiple headers

The [`headers`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/headers.html) function allows
you to add several headers at once:

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="46-52"}

#### Add a single header

The [`header`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/header.html) function allows you
to append a single header.

#### Use `basicAuth` or `bearerAuth` for authorization

The `basicAuth` and `bearerAuth` functions add the `Authorization` header with a corresponding HTTP scheme.

> For advanced authentication configuration, see [](client-auth.md).

### Cookies {id="cookies"}

To send cookies, use the
[`cookie()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/cookie.html) function:

```kotlin
```

{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="55-64"}

Ktor also provides the [`HttpCookies`](client-cookies.md) plugin that allows you to keep cookies between calls. If this
plugin is installed, cookies added using the `cookie()` function are ignored.

## Set request body {id="body"}

To set the request body, call the `setBody()` function provided by
[`HttpRequestBuilder`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html).
This function accepts different types of payloads, including plain text, arbitrary class instances, form data, and byte
arrays.

### Text {id="text"}

Sending plain text as body can be implemented in the following way:

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.post("http://localhost:8080/post") {
    setBody("Body content")
}
```

### Objects {id="objects"}

With the enabled [`ContentNegotiation`](client-serialization.md) plugin, you can send a class instance within a request
body as JSON. To do this, pass a class instance to the `setBody()` function and set the content type to
`application/json`using the [`contentType()`](https://api.ktor.io/ktor-http/io.ktor.http/content-type.html) function:

```kotlin
```

{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="33-36"}

For more information, see [](client-serialization.md).

### Form parameters {id="form_parameters"}

The Ktor client provides the
[`submitForm()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/submit-form.html)
function for sending form parameters with the `application/x-www-form-urlencoded` type. The following example
demonstrates its usage:

```kotlin
```

{src="snippets/client-submit-form/src/main/kotlin/com/example/Application.kt" include-lines="16-25"}

* `url` specifies a URL for making a request.
* `formParameters` is a set of form parameters built using `parameters`.

For the full example, see [client-submit-form](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-submit-form).

> To send form parameters encoded in a URL, set `encodeInQuery` to `true`.


### Upload a file {id="upload_file"}

If you need to send a file with a form, you can use the following approaches:

* Use the
  [`.submitFormWithBinaryData()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/submit-form-with-binary-data.html)
  function. In this case, a boundary will be generated automatically.
* Call the `post` function and pass the
  [`MultiPartFormDataContent`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/-multi-part-form-data-content/index.html)
  instance to the `setBody` function. The `MultiPartFormDataContent` constructor also allows you to pass a boundary
  value.

For both approaches, you need to build form data using the
[`formData {}`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/form-data.html) function.

#### Using `.submitFormWithBinaryData()`

The `.submitFormWithBinaryData()` function automatically generates a boundary and is suitable for simple use cases where
the file content is small enough to be safely read into memory using `.readBytes()`.

```kotlin
```

{src="snippets/client-upload/src/main/kotlin/com/example/Application.kt" include-lines="13-24"}

For the full example,
see [client-upload](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload).

#### Using `MultiPartFormDataContent`

To stream large or dynamic content efficiently, you can use `MultiPartFormDataContent` with an `InputProvider`.
`InputProvider` allows you to supply file data as a buffered stream rather than loading it entirely into memory, making
it well-suited for large files. With `MultiPartFormDataContent`, you can also monitor upload progress using the
`onUpload` callback.

```kotlin
```

{src="snippets/client-upload-progress/src/main/kotlin/com/example/Application.kt" include-lines="24-48"}

In multiplatform projects, you can use `SystemFileSystem.source()` with `InputProvider`:

```kotlin
InputProvider { SystemFileSystem.source(Path("ktor_logo.png")).buffered() }
```

You can also construct a `MultiPartFormDataContent` with a custom boundary and content type manually:

```kotlin
```

{src="snippets/client-upload-progress/src/main/kotlin/com/example/Application.kt" include-lines="54-58"}

For the full example, see
[client-upload-progress](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload-progress).

### Binary data {id="binary"}

To send binary data with the `application/octet-stream` content type, pass the
[`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) instance to the
`setBody()` function.
For example, you can use the [`File.readChannel()`](https://api.ktor.io/ktor-utils/io.ktor.util.cio/read-channel.html)
function to open a read channel for a file:

```kotlin
```

{src="snippets/client-upload-binary-data/src/main/kotlin/com/example/Application.kt" include-lines="14-16"}

For the full example, see
[client-upload-binary-data](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload-binary-data).

## Parallel requests {id="parallel_requests"}

By default, when you send multiple requests sequentially, the client suspends each call until the previous one
completes. To perform multiple requests concurrently, use the
[`launch()`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html)
or [`async()`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html)
functions. The following example demonstrates how to execute two requests in parallel using `async()`:

```kotlin
```

{src="snippets/client-parallel-requests/src/main/kotlin/com/example/Application.kt" include-lines="12,19-23,28"}

For the full example, see
[client-parallel-requests](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-parallel-requests).

## Cancel a request {id="cancel-request"}

To cancel a request, cancel the coroutine running that request.
The [`launch()`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html)
function returns a `Job` that can be used to cancel the running coroutine:

```kotlin
import kotlinx.coroutines.*

val client = HttpClient(CIO)
val job = launch {
    val requestContent: String = client.get("http://localhost:8080")
}
job.cancel()
```

For more details, see [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html).

[//]: # (title: Receiving responses)

<show-structure for="chapter" depth="3"/>

<link-summary>
Learn how to receive responses, get a response body and obtain response parameters.
</link-summary>

All functions used to [make an HTTP request](client-requests.md) (`request`, `get`, `post`, etc.) allow you to receive a
response as
an [`HttpResponse`](https://api.ktor.io/ktor-client-core/io.ktor.client.statement/-http-response/index.html)
object.

`HttpResponse` exposes the API required to get a [response body](#body) in various ways (raw bytes, JSON
objects, etc.) and obtain [response parameters](#parameters), such as a status code, content type, and headers.
For example, you can receive `HttpResponse` for a `GET` request without parameters in the following way:

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}

## Receive response parameters {id="parameters"}

The [`HttpResponse`](https://api.ktor.io/ktor-client-core/io.ktor.client.statement/-http-response/index.html)
class allows you to get various response parameters, such as a status code, headers, HTTP version, and more.

### Status code {id="status"}

To get the status code of a response, use the
[`HttpResponse.status`](https://api.ktor.io/ktor-client-core/io.ktor.client.statement/-http-response/status.html)
property:

```kotlin
```

{src="snippets/_misc_client/ResponseTypes.kt" include-lines="1-4,9,11,15-17"}

### Headers {id="headers"}

The [
`HttpResponse.headers`](https://api.ktor.io/ktor-client-core/io.ktor.client.statement/-http-response/index.html)
property allows you to get a [`Headers`](https://api.ktor.io/ktor-http/io.ktor.http/-headers/index.html) map containing
all response headers.

Additionally, the `HttpResponse` class exposes the following functions for receiving specific header values:

* `contentType()` for the `Content-Type` header value.
* `charset()` for a charset from the `Content-Type` header value.
* `etag()` for the `E-Tag` header value.
* `setCookie()` for the `Set-Cookie` header value.
  > Ktor also provides the [`HttpCookies`](client-cookies.md) plugin that allows you to keep cookies between calls.


#### Split header values

If a header can contain multiple comma — or semicolon — separated values, you can use the `.getSplitValues()` function
to retrieve all split values from a header:

```kotlin
val httpResponse: HttpResponse = client.get("https://ktor.io/")
val headers: Headers = httpResponse.headers

val splitValues = headers.getSplitValues("X-Multi-Header")!!
// ["1", "2", "3"]
```

Using the usual `get` operator returns values without splitting:

```kotlin
val values = headers["X-Multi-Header"]!!
// ["1, 2", "3"]
```

## Receive response body {id="body"}

### Raw body {id="raw"}

To receive a raw body of a response, call the `body` function and pass the required type as a parameter. The code
snippet below shows how to receive a raw body as a [`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/):

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" include-lines="11,12"}

Similarly, you can get a body as a [`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/):

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" include-lines="11,13"}

A [runnable example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-file)
below shows how to get a response as a `ByteArray` and save it to a file:

```kotlin
```
{src="snippets/client-download-file/src/main/kotlin/com/example/Downloader.kt" include-lines="12-24"}

The [`onDownload()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/on-download.html) extension
function in the example above is used to display download progress.

For non-streaming requests, the response body is automatically loaded and cached in memory, allowing repeated access.
While this is efficient for small payloads, it may lead to high memory usage with large responses.

To handle large responses efficiently, use a [streaming approach](#streaming),
which processes the response incrementally without saving it in memory.

### JSON object {id="json"}

With the [ContentNegotiation](client-serialization.md) plugin installed, you can deserialize JSON data into a data class
when receiving responses:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="39"}

To learn more, see [](client-serialization.md#receive_send_data).

> The ContentNegotiation plugin is available for both the [client](client-serialization.md) and
> the [server](server-serialization.md). Ensure to use the proper one for your case.

### Multipart form data {id="multipart"}

When receiving a response that contains multipart form data, you can read its body as a
[`MultiPartData`](https://api.ktor.io/ktor-http/io.ktor.http.content/-multi-part-data/index.html) instance.
This allows you to process form fields and files included in the response.

The example below demonstrates how to handle both text form fields and file uploads from a multipart response:

```kotlin
```

{src="snippets/_misc_client/ResponseTypes.kt" include-lines="20-39"}

#### Form fields

`PartData.FormItem` represents a form field, which values can be accessed through the value property:

```kotlin
```

{src="snippets/_misc_client/ResponseTypes.kt" include-lines="25-30,37"}

#### File uploads

`PartData.FileItem` represents a file item. You can handle file uploads as byte streams:


```kotlin
```

{src="snippets/_misc_client/ResponseTypes.kt" include-lines="25,31-37"}

#### Resource cleanup

Once the form processing is complete, each part is disposed of using the `.dispose()` function to free resources.

```kotlin
```

{src="snippets/_misc_client/ResponseTypes.kt" include-lines="38"}

### Streaming data {id="streaming"}

By default, calling `HttpResponse.body()` loads the full response into memory. For large responses or file downloads,
it’s often better to process data in chunks without waiting for the full body.

Ktor provides several ways to do this using [`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html)
and I/O utilities.

#### Sequential chunk processing

To process the response sequentially in chunks, use `HttpStatement` with
a scoped [`execute`](https://api.ktor.io/ktor-client-core/io.ktor.client.statement/-http-statement/execute.html)
block.

The following example demonstrates reading a response in chunks and saving it to a file:

```kotlin
```
{src="snippets/client-download-streaming/src/main/kotlin/com/example/Application.kt" include-lines="15-37"}

Using `ByteReadChannel.readRemaining()` retrieves all available bytes in the channel, while
`Source.transferTo()` directly writes the data to the file, reducing unnecessary allocations.

> For the full streaming example, see
> [client-download-streaming](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-streaming).

#### Writing the response directly to a file

For simple downloads where chunk-by-chunk processing is not needed, you can choose one of the following approaches:

- [Copy all bytes to a `ByteWriteChannel` and close](#copyAndClose).
- [Copy to a `RawSink`](#readTo).

##### Copy all bytes to a `ByteWriteChannel` and close {id="copyAndClose"}

The [`ByteReadChannel.copyAndClose()`](https://api.ktor.io/ktor-io/io.ktor.utils.io/copy-and-close.html) function
copies all remaining bytes from a `ByteReadChannel` to a `ByteWriteChannel` and then closes both channels automatically:

```Kotlin
client.prepareGet("https://httpbin.org/bytes/$fileSize").execute { httpResponse ->
    val channel: ByteReadChannel = httpResponse.body()
    channel.copyAndClose(file.writeChannel())
    println("A file saved to ${file.path}")
}
```

This is convenient for full file downloads where you don’t need to manually manage channels.

##### Copy to a `RawSink` {id="readTo"}

[//]: # (TODO: Add API link)

The [`ByteReadChannel.readTo()`]()
function writes bytes directly to a `RawSink` without intermediate buffers:

```kotlin
val file = File.createTempFile("files", "index")
val stream = file.outputStream().asSink()

client.prepareGet(url).execute { httpResponse ->
    val channel: ByteReadChannel = httpResponse.body()
    channel.readTo(stream)
}
println("A file saved to ${file.path}")

```

Unlike `.copyAndClose()`, the sink remains open after writing and it is only closed automatically if an error occurs
during the transfer.


> For converting between Ktor channels and types like `RawSink`, `RawSource`, or `OutputStream`, see
> [I/O interoperability](io-interoperability.md).
>
{style="tip"}
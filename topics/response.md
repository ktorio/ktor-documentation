[//]: # (title: Receiving responses)

<show-structure for="chapter" depth="2"/>

<link-summary>
Learn how to receive responses: get a response body in various ways (raw bytes, JSON objects, etc.) and obtain response parameters (a status code, content type, etc.).
</link-summary>

All functions used to [make an HTTP request](request.md) (`request`, `get`, `post`, etc.) allow you to receive a response as an [HttpResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html) object. `HttpResponse` exposes the API required to get a [response body](#body) in various ways (raw bytes, JSON objects, etc.) and obtain [response parameters](#parameters), such as a status code, content type, headers, and so on. For example, you can receive `HttpResponse` for a `GET` request without parameters as follows:

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}

## Receive response parameters {id="parameters"}

The [HttpResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html) class allows you to get various response parameters, such as a status code, headers, HTTP version, and so on.

### Status code {id="status"}

To get the status code of a response, use the [HttpResponse.status](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/status.html) property, for example:

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" include-lines="1-4,9,11,15-17"}


### Headers {id="headers"}
The [HttpResponse.headers](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html) property allows you to get a [Headers](https://api.ktor.io/ktor-http/io.ktor.http/-headers/index.html) map containing all response headers. `HttpResponse` also exposes a bunch of specific functions for receiving specific header values, for example:
* `contentType` for the `Content-Type` header value
* `charset` for a charset from the `Content-Type` header value.
* `etag` for the `E-Tag` header value.
* `setCookie` for the `Set-Cookie` header value.
  > Ktor also provides the [HttpCookies](http-cookies.md) plugin that allows you to keep cookies between calls.






## Receive response body {id="body"}

### Raw body {id="raw"}

To receive a raw body of a response, call the `body` function and pass the required type as a parameter. The code snippet below shows how to receive a raw body as [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/):

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" include-lines="11,12"}

Similarly, you can get a body as [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/):

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" include-lines="11,13"}

A [runnable example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-file) below shows how to get a response as a byte array and save it to a file:
```kotlin
```
{src="snippets/client-download-file/src/main/kotlin/com/example/Downloader.kt" include-lines="12-24"}

> The [onDownload](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/on-download.html) extension function in the example above is used to display download progress.

### JSON object {id="json"}

With the [ContentNegotiation](serialization-client.md) plugin installed, you can deserialize JSON data into a data class when receiving responses, for example:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="39"}

To learn more, see [](serialization-client.md#receive_send_data).

Note: there are ContentNegotiation plugins for both client and server, make sure to use proper one (client in this case).

### Streaming data {id="streaming"}

When you call the `HttpResponse.body` function to get a body, Ktor processes a response in memory and returns a full response body. If you need to get chunks of a response sequentially instead of waiting for the entire response, use `HttpStatement` with scoped [execute](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-statement/execute.html) block. A [runnable example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-streaming) below shows how to receive a response content in chunks (byte packets) and save them in a file:

```kotlin
```
{src="snippets/client-download-streaming/src/main/kotlin/com/example/Application.kt" include-lines="15-31"}

In this example, [ByteReadChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) is used to read data asynchronously using byte packets ([ByteReadPacket](https://api.ktor.io/ktor-io/io.ktor.utils.io.core/-byte-read-packet/index.html)) and append the content of these packets to the content of a file.

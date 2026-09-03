[//]: # (title: Handling requests)

<show-structure for="chapter" depth="3"/>

<link-summary>Learn how to handle incoming requests inside route handlers.</link-summary>

Ktor allows you to handle incoming requests and send [responses](server-responses.md)
from [route handlers](server-routing.md#define_route).

Each route handler provides an [`ApplicationCall`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call/index.html)
through the `call` property. An `ApplicationCall` represents a single HTTP exchange and provides access to both the
incoming request and the outgoing response.

Within a route handler, you can use `ApplicationCall` to perform the following:

* Access [request information](#request_information), such as headers, cookies, and connection details.
* Retrieve [path parameters](#path_parameters).
* Retrieve [query parameters](#query_parameters).
* Receive [request body content](#body_contents), such as data objects, form parameters, and files.

## General request information {id="request_information"}

You can access request data through the [`call.request`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call/request.html) property. This returns an [`ApplicationRequest`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/index.html)
instance, which provides access to low-level HTTP request information.

For example, you can retrieve the request URI in a GET request handler using `call.request.uri`:

```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```

The [`call.respondText()`](server-responses.md#plain-text) function sends a plain text response back to the client.

### Headers {id="headers"}

To access all HTTP request headers, use the [`ApplicationRequest.headers`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/headers.html) property.

For convenience, Ktor also provides dedicated extension functions for accessing commonly used headers, such as 
[`.acceptEncoding()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/accept-encoding.html),
[`.contentType()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/content-type.html), and
[`.cacheControl()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/cache-control.html).

### Cookies {id="cookies"}

To access cookies sent with the request, use the [`ApplicationRequest.cookies`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/cookies.html)
property.

> For more information on handling sessions using cookies, see the [Sessions](server-sessions.md) section.
> 
{style="tip"}

### Connection details

Use the [`ApplicationRequest.local`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/local.html)
property to access connection details such as the host, port, and scheme.

### `X-Forwarded-` headers

To collect information about a request passed through an HTTP proxy or a load balancer, install the [](server-forward-headers.md)
plugin. You can then access this information through the [`ApplicationRequest.origin`](https://api.ktor.io/ktor-server-core/io.ktor.server.plugins/origin.html)
property.

## Path parameters {id="path_parameters"}

When handling requests, you can retrieve [path parameter](server-routing.md#path_parameter) values using the 
`ApplicationCall.parameters` property.

For example, `call.parameters["login"]` returns `"admin"` for a request to `/user/admin`:

```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}

## Query parameters {id="query_parameters"}

To retrieve parameters of a URL query string, use the
[`ApplicationRequest.queryParameters`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/query-parameters.html)
property.

The following example accesses the `price` query parameter from a request made to `/products?price=asc`:

```kotlin
```
{src="snippets/_misc/QueryParameter.kt"}

You can also get the entire query string using the [`ApplicationRequest.queryString()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/query-string.html) function.

## Required request parameters

When handling requests, it is common to extract values from [path parameters](#path_parameters),
[query parameters](#query_parameters), [headers](#headers), or [cookies](#cookies) and validate that they are present
before continuing request processing.

Instead of manually checking for missing values in every route handler, Ktor provides the following helper functions
that simplify accessing required request data:

* [`.requireQueryParameter()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/require-query-parameter.html)
  retrieves a required query parameter from the request URL.
* [`.requireHeader()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/require-header.html) retrieves a required
  HTTP header value.
* [`.requireCookie()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/require-cookie.html) retrieves a required
  cookie value, optionally decoding it using the specified encoding.
* [`.requirePathParameter()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/require-path-parameter.html)
  retrieves a required path parameter from the route definition.

Each function returns a non-null value or throws `MissingRequestParameterException` if the requested value is missing.

```kotlin
post("/checkout/{cartId}") {
    val userId = call.requireCookie("userId")
    val cartId = call.requirePathParameter("cartId")
    val amount = call.requireQueryParameter("amount").toLong()

    // Business logic
}
```

## Body contents {id="body_contents"}

To access the request body, use Ktor's receive functions. The appropriate function depends on whether you need
[raw content](#raw), [a deserialized object](#objects), [form parameters](#form_parameters), or [multipart data](#form_data).

### Raw payload {id="raw"}

To access the raw body payload and parse it manually, use the [`ApplicationCall.receive()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive.html) function that accepts
a type of payload to be received.

Suppose a client sends the following HTTP request:

```HTTP
```
{src="snippets/post-raw-data/post.http" include-lines="1-4"}

You can receive the request body as a [`String`](#string), [`ByteArray`](#bytearray), or [`ByteReadChannel`](#bytereadchannel).

#### `String`

To receive a request body as text, use the `.receive<String>()` or [`.receiveText()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-text.html) function:
```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="14-17"}

#### `ByteArray`

To receive the body of a request as a byte array, use the `.receive<ByteArray>()` function:

```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="19-23"}

#### `ByteReadChannel`

To read the body asynchronously as a [`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html),
use the `.receive<ByteReadChannel>()` or [`.receiveChannel()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-channel.html) function:

```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="24-28"}

You can also use a `ByteReadChannel` to upload a file:

```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="30-34"}

> For converting between Ktor channels and types like `RawSink`, `RawSource`, or `OutputStream`, see
> [I/O interoperability](io-interoperability.md).
>
{style="tip"}

> For the full example, see [post-raw-data](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/post-raw-data).
> 
{style="tip"}

### Objects {id="objects"}

Ktor provides the [`ContentNegotiation`](server-serialization.md) plugin to negotiate the media type of request and
deserialize content to an object of a required type.

To receive and convert content for a request, use the
[`ApplicationCall.receive()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive.html) function with
the expected type:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="39-43"}

If the request content can deserialize to `null`, use a nullable type argument:

```kotlin
val customer = call.receive<Customer?>()
```

> For more information, see [](server-serialization.md).
> 
{style="tip"}

### Form parameters {id="form_parameters"}

You can receive form parameters sent with both `x-www-form-urlencoded` and `multipart/form-data` types using the
[`.receiveParameters()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-parameters.html) function.

For example, suppose a client sends the following request:

```HTTP
```
{src="snippets/post-form-parameters/post.http"}

You can access parameter values in code as follows:

```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/formparameters/Application.kt" include-lines="12-16"}

> For the complete example, see [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/post-form-parameters).
> 
{style="tip"}

### Multipart form data {id="form_data"}

To receive a file sent as a part of a multipart request, use
the [`.receiveMultipart()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-multipart.html)
function.

Multipart request data is processed sequentially, so you can't directly access a specific part of it.
Each part can represent a form field, a file, or other binary content, so handle each type separately.

The following example receives a form field and a file, then saves the file to the local file system:

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="3-39"}

#### Default file size limit

By default, binary and file parts are limited to 50MiB. If a part exceeds this limit, Ktor throws an `IOException`.

To override the default limit for a call, pass the `formFieldLimit` parameter to the `.receiveMultipart()` function:

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="17"}

This example sets the limit to 100 MiB.

#### Form fields

`PartData.FormItem` represents a form field. You can access its value through the `value` property:

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="20-23,32"}

#### File uploads

`PartData.FileItem` represents an uploaded file. You can handle file uploads as byte streams. Use the [`.provider()`](https://api.ktor.io/ktor-http/io.ktor.http.content/-part-data/-file-item/provider.html)
function to access the file content as a `ByteReadChannel` and stream it to a destination:

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="20,25-29,32"}

With the `.copyAndClose()` function, you write the file content to the specified destination while ensuring proper
resource cleanup.

If the request includes a `Content-Length` [header value](#request_information), you can use it to inspect the size of the complete
request body:

```kotlin
post("/upload") {
    val contentLength = call.request.header(HttpHeaders.ContentLength)
    // ...
}
```

For multipart requests, `Content-Length` represents the entire multipart body, not the size of an individual uploaded
file.

#### Resource cleanup

Once form processing is complete, dispose of each multipart part using the `.dispose()` function to free its
resources:

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="33"}

> To learn how to run this sample, see [upload-file](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/upload-file).
> 
{style="tip"}
[//]: # (title: Handling requests)

<show-structure for="chapter" depth="3"/>

<link-summary>Learn how to handle incoming requests inside route handlers.</link-summary>

Ktor allows you to handle incoming requests and send [responses](server-responses.md)
inside [route handlers](server-routing.md#define_route).

Route handlers operate on an [`ApplicationCall`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call/index.html),
which represents a single HTTP exchange between the client and the server. It is available in route handlers through 
the `call` property and contains both the incoming request (`ApplicationRequest`) and the outgoing response
(`ApplicationResponse`).

Within a route handler, you can use the `ApplicationCall` to:

* Access [request information](#request_information), such as headers, cookies, and connection details.
* Retrieve [path parameter](#path_parameters) values.
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

To access the cookies sent with the request, use the [`ApplicationRequest.cookies`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/cookies.html)
property.

> For more information on handling sessions using cookies, see the [Sessions](server-sessions.md) section.
> 
{style="tip"}

### Connection details

Use the [`ApplicationRequest.local`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/local.html)
property to get access to connection details such as a host name, port, and scheme.

### `X-Forwarded-` headers

To obtain information about a request passed through an HTTP proxy or a load balancer, install the [](server-forward-headers.md)
plugin and use the [`ApplicationRequest.origin`](https://api.ktor.io/ktor-server-core/io.ktor.server.plugins/origin.html)
property.


## Path parameters {id="path_parameters"}

When handling requests, you can retrieve [path parameter](server-routing.md#path_parameter) values using the 
`ApplicationCall.parameters` property.

For example, in the code snippet below, `call.parameters["login"]` returns `"admin"` for the `/user/admin` path:

```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}


## Query parameters {id="query_parameters"}

To retrieve parameters of a URL query string, you can use the
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

[//]: # (TODO: Add API links)

* `ApplicationCall.requireQueryParameter()` — retrieves a required query parameter from the request URL. Throws if the
  parameter is missing.
* `ApplicationCall.requireHeader()` — retrieves a required HTTP header value. Throws if the header is not present in
  the request.
* `ApplicationCall.requireCookie()` — retrieves a required cookie value, optionally decoding it using the specified
  encoding. Throws if the cookie is missing.
* `RoutingCall.requirePathParameter()` — retrieves a required path parameter from the route definition. Throws if
  the parameter is not present in the matched route.

Each function returns a non-null value or throws `MissingRequestParameterException` if the value is missing.

```kotlin
post("/checkout/{cartId}") {
    val userId = call.requireCookie("userId")
    val cartId = call.requirePathParameter("cartId")
    val amount = call.requireQueryParameter("amount").toLong()

    // Business logic
}
```

## Body contents {id="body_contents"}
This section shows how to receive body contents sent with `POST`, `PUT`, or `PATCH`.

### Raw payload {id="raw"}

To access the raw body payload and parse it manually, use the [`ApplicationCall.receive()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive.html) function that accepts
a type of payload to be received. Suppose you have the following HTTP request:

```HTTP
```
{src="snippets/post-raw-data/post.http" include-lines="1-4"}

You can receive the body of this request as an object of the specified type in one of the following ways:

- **String**

   To receive a request body as a String value, use `call.receive<String>()`.
   You can also use [`.receiveText()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-text.html) to achieve the same result:
   ```kotlin
   ```
   {src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="14-17"}
- **ByteArray**

   To receive the body of a request as a byte array, call `call.receive<ByteArray>()`:
   ```kotlin
   ```
   {src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="19-23"}
- **ByteReadChannel**

   You can use `call.receive<ByteReadChannel>()` or [`.receiveChannel()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-channel.html) to receive [`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) that enables asynchronous reading of byte sequences:
   ```kotlin
   ```
   {src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="24-28"}

   The sample below shows how to use `ByteReadChannel` to upload a file:
   ```kotlin
   ```
   {src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" include-lines="30-34"}

> For converting between Ktor channels and types like `RawSink`, `RawSource`, or `OutputStream`, see
> [I/O interoperability](io-interoperability.md).
>
{style="tip"}

> For the full example, see [post-raw-data](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/post-raw-data).


### Objects {id="objects"}

Ktor provides a [ContentNegotiation](server-serialization.md) plugin to negotiate the media type of request and
deserialize content to an object of a required type.

To receive and convert content for a request, call the
[`ApplicationCall.receive()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive.html) function that
accepts a data class as a parameter:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="39-43"}

> For more information, see [](server-serialization.md).

### Form parameters {id="form_parameters"}
Ktor allows you to receive form parameters sent with both `x-www-form-urlencoded` and `multipart/form-data` types using the [receiveParameters](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-parameters.html) function. The example below shows an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) `POST` request with form parameters passed in a body:
```HTTP
```
{src="snippets/post-form-parameters/post.http"}

You can obtain parameter values in code as follows:
```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/formparameters/Application.kt" include-lines="12-16"}

> For the full example, see [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/post-form-parameters).

### Multipart form data {id="form_data"}

To receive a file sent as a part of a multipart request, call
the [`.receiveMultipart()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/receive-multipart.html)
function and then loop over each part as required.

Multipart request data is processed sequentially, so you can't directly access a specific part of it. Additionally,
these requests can contain different types of parts, such as form fields, files, or binary data, which need to
be handled differently.

The example demonstrates how to receive a file and save it to a file system:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="3-39"}

#### Default file size limit

By default, the allowed size for binary and file items that can be received is limited to 50MiB. If a received file
or binary item exceeds the 50MiB limit, an `IOException` is thrown.

To override the default form field limit, pass the `formFieldLimit` parameter when calling `.receiveMultipart()`:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="17"}

In this example, the new limit is set to 100MiB.

#### Form fields

`PartData.FormItem` represents a form field, which values can be accessed through the `value` property:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="20-23,32"}

#### File uploads

`PartData.FileItem` represents a file item. You can handle file uploads as byte streams:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="20,25-29,32"}

The [`.provider()`](https://api.ktor.io/ktor-http/io.ktor.http.content/-part-data/-file-item/provider.html)
function returns a `ByteReadChannel`, which allows you to read data incrementally.
Using the `.copyAndClose()` function, you then write the file content to the specified destination
while ensuring proper resource cleanup.

To determine the uploaded file size, you can get the `Content-Length` [header value](#request_information) inside the `post` handler:

```kotlin
post("/upload") {
    val contentLength = call.request.header(HttpHeaders.ContentLength)
    // ...
}
```

#### Resource cleanup

Once the form processing is complete, each part is disposed of using the `.dispose()` function to free resources.

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="33"}

> To learn how to run this sample, see
> [upload-file](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/upload-file).
[//]: # (title: Handling requests)

<show-structure for="chapter" depth="3"/>

<link-summary>Learn how to handle incoming requests inside route handlers.</link-summary>

Ktor allows you to handle incoming requests and send [responses](server-responses.md)
inside [route handlers](server-routing.md#define_route). You can perform various actions when handling requests:

* Get [request information](#request_information), such as headers, cookies, and so on.
* Get [path parameter](#path_parameters) values.
* Get parameters of a [query string](#query_parameters).
* Receive [body contents](#body_contents), for example, data objects, form parameters, and files.

## General request information {id="request_information"}
Inside a route handler, you can get access to a request using the [`call.request`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call/request.html)
property. This returns the [`ApplicationRequest`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/index.html)
instance and provides access to various request parameters. For example, the code snippet below shows how to get a
request URI:

```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```
The [`call.respondText()`](server-responses.md#plain-text) method is used to send a response back to the client.

The [`ApplicationRequest`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/index.html) object allows you to get access to various request data, for example:

* **Headers**

  To access all request headers, use the [`ApplicationRequest.headers`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/headers.html) property.
  You can also get access to specific headers using dedicated extension functions, such as `acceptEncoding`,
  `contentType`, `cacheControl`, and so on.

* **Cookies**  

  The [`ApplicationRequest.cookies`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/cookies.html)
  property provides access to cookies related to a request. To learn how to handle sessions using cookies, see the
  [Sessions](server-sessions.md) section.

* **Connection details**

  Use the [`ApplicationRequest.local`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/local.html)
  property to get access to connection details such as a host name, port, scheme, and so on.

* **`X-Forwarded-` headers**

  To get information about a request passed through an HTTP proxy or a load balancer, install the [](server-forward-headers.md)
  plugin and use the [`ApplicationRequest.origin`](https://api.ktor.io/ktor-server-core/io.ktor.server.plugins/origin.html)
  property.


## Path parameters {id="path_parameters"}
When handling requests, you can get access to [path parameter](server-routing.md#path_parameter) values using the `call.parameters` property. For
example, `call.parameters["login"]` in the code snippet below will return _admin_ for the `/user/admin` path:

```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}


## Query parameters {id="query_parameters"}

To get access to parameters of a <emphasis tooltip="query_string">query string</emphasis>, you can use the
[`ApplicationRequest.queryParameters()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/-application-request/query-parameters.html)
property. For example, if a request is made to `/products?price=asc`, you can access the `price` query parameter in
the following way:

```kotlin
```
{src="snippets/_misc/QueryParameter.kt"}

You can also get the entire query string using the [`ApplicationRequest.queryString()`](https://api.ktor.io/ktor-server-core/io.ktor.server.request/query-string.html) function.


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

By default, the allowed size for binary and file items that can be received is limited to 50MB. If a received file
or binary item exceeds the 50MB limit, an `IOException` is thrown.

To override the default form field limit, pass the `formFieldLimit` parameter when calling `.receiveMultipart()`:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="17"}

In this example, the new limit is set to 100MB.

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
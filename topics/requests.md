[//]: # (title: Handling requests)

<excerpt>Learn how to handle incoming requests inside route handlers: get request information, receive body contents, etc.</excerpt>

Ktor allows you to handle incoming requests and send [responses](responses.md) inside [route handlers](Routing_in_Ktor.md#define_route). You can perform various actions when handling requests:
* Get [request information](#request_information), such as headers, cookies, and so on.
* Get [path parameter](#path_parameters) values.
* Get parameters of a [query string](#query_parameters).
* Receive [body contents](#body_contents), for example, data objects, form parameters, and files.

## General request information {id="request_information"}
Inside a route handler, you can get access to a request using the [call.request](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-call/request.html) property. This returns the [ApplicationRequest](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/index.html) instance and provides access to various request parameters. For example, the code snippet below shows how get a request URI:
```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```
> The [call.respondText](responses.md#plain-text) method is used to send a response back to the client.

The [ApplicationRequest](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/index.html) object allows you to get access to various request data, for example:
* Headers  
  To access all request headers, use the [ApplicationRequest.headers](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/headers.html) property. You can also get access to specific headers using dedicated extension functions, such as `acceptEncoding`, `contentType`, `cacheControl`, and so on.
* Cookies  
  The [ApplicationRequest.cookies](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/cookies.html) property provides access to cookies related to a request. To learn how to handle sessions using cookies, see the [Sessions](sessions.md) section.
* Connection details  
  Use the [ApplicationRequest.local](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/local.html) property to get access to connection details such as a host name, port, scheme, and so on.
* `X-Forwarded-` headers  
  To get information about a request passed through an HTTP proxy or a load balancer, install the [](forward-headers.md) plugin and use the [ApplicationRequest.origin](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.plugins/origin.html) property.


## Path parameters {id="path_parameters"}
When handling requests, you can get access to [path parameter](Routing_in_Ktor.md#path_parameter) values using the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _admin_ for the `/user/admin` path:
```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}


## Query parameters {id="query_parameters"}

To get access to parameters of a <emphasis tooltip="query_string">query string</emphasis>, you can use the [ApplicationRequest.queryParameters](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/query-parameters.html) property. For example, if a request is made to `/products?price=asc`, you can access the `price` query parameter in this way:

```kotlin
```
{src="snippets/_misc/QueryParameter.kt"}

You can also obtain the entire query string using the [ApplicationRequest.queryString](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/query-string.html) function.


## Body contents {id="body_contents"}
This section shows how to receive body contents sent with `POST`, `PUT`, or `PATCH`.

### Raw payload {id="raw"}
To access the raw body payload and parse it by yourself, you can use the following functions:
* [receiveText](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/receive-text.html)
* [receiveChannel](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/receive-channel.html)

Suppose you have the following HTTP request:

```HTTP
```
{src="snippets/post-raw-data/post.http" lines="1-4"}

To receive the body of this request as a String value, use `ApplicationCall.receiveText`:

```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" lines="12-15"}

You can also use `ApplicationCall.receiveChannel` to receive [ByteReadChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) that allows asynchronous reading of byte sequences:

```kotlin
```
{src="snippets/post-raw-data/src/main/kotlin/com/example/Application.kt" lines="17-21"}

You can find the full example here: [post-raw-data](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/post-raw-data).


### Objects {id="objects"}
Ktor provides a [ContentNegotiation](serialization.md) plugin to negotiate the media type of request and deserialize content to an object of a required type. To receive and convert content for a request, call the [ApplicationCall.receive](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/receive.html) function that accepts a data class as a parameter:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="38-42"}

You can learn more from [](serialization.md).

### Form parameters {id="form_parameters"}
Ktor allows you to receive form parameters sent with both `x-www-form-urlencoded` and `multipart/form-data` types using the [receiveParameters](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/receive-parameters.html) function. The example below shows an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) `POST` request with form parameters passed in a body:
```HTTP
```
{src="snippets/post-form-parameters/post.http"}

You can obtain parameter values in code as follows:
```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/com/example/Application.kt" lines="12-16"}

You can find the full example here: [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/post-form-parameters).


### Multipart form data {id="form_data"}
If you need to receive a file sent as a part of a multipart request, call the [receiveMultipart](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/receive-multipart.html) function and then loop over each part as required. In the example below, `PartData.FileItem` is used to receive a file as a byte stream.
```kotlin
```
{src="/snippets/upload-file/src/main/kotlin/com/example/UploadFile.kt" include-symbol="main"}

Learn how to run this sample from [upload-file](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/upload-file).

> To determine the uploaded file size, you can get the `Content-Length` [header value](#request_information) inside the `post` handler:
> ```kotlin
> post("/upload") {
>     val contentLength = call.request.header(HttpHeaders.ContentLength)
>     // ...
> }
> ```
> 
{type="tip"}

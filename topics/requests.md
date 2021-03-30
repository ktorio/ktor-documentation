[//]: # (title: Requests)

Ktor allows you to handle incoming requests and send [responses](responses.md) inside [route handlers](Routing_in_Ktor.md#define_route). You can perform various actions when handing requests:
* Get [request information](#request_information), such as headers, cookies, and so on.
* Get [route parameter](#route_parameters) values.
* Obtain parameters of a [query string](#query_parameters).
* Receive [body contents](#body_contents) (raw, form data, data objects, file uploads).

## General request information {id="request_information"}
Inside a route handler, you can get access to request data using the [call.request](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/request.html) property. This property returns the [ApplicationRequest](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/index.html) and provides access to various request parameters. For example, the code snippet below shows how get a request URI:
```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```
> The [call.respondText](responses.md) method is used to send a response back to the client.

The [ApplicationRequest](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/index.html) object allows you to get access to various request data, for example:
* Headers  
  To access all request headers, use the `ApplicationRequest.headers` property. You can also get access to specific headers using dedicated extension functions, such as `acceptEncoding`, `contentType`, `cacheControl`, and so on.
* Cookies  
  The `ApplicationRequest.cookies` property provides access to cookies related to a request. To learn how to handle sessions using cookies, use the [Sessions](sessions.md) feature.
* Connection details  
  Use the `ApplicationRequest.local` property to get access to connection details such as a host name, port, scheme and so on.
* `X-Forwarded-` headers  
  To get information about a request passed through an HTTP proxy or a load balancer, install the [](forward-headers.md) feature and use the `ApplicationRequest.origin` property.


## Route parameters {id="route_parameters"}
When handling requests, you can get access to [route parameter](Routing_in_Ktor.md#route_parameter) values using the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _admin_ for the `/user/admin` path:
```kotlin
get("/user/{login}") {
    if(call.parameters["login"] == "admin") {
        // ...
    }
}
```


## Query parameters {id="query_parameters"}

If you need to get access to parameters of a <emphasis tooltip="query_string">query string</emphasis>, you can use the [ApplicationRequest.queryParameters](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/query-parameters.html) property. For example, if a request is made to `/products?price=asc`, you can access the `price` query parameter like this:

```kotlin
get("/products") {
    if (call.request.queryParameters["price"] == "asc") {
        // Sort products from the lowest price to the highest
    }
}
```
You can also obtain the entire query string using the [ApplicationRequest.queryString](https://api.ktor.io/%ktor_version%/io.ktor.request/query-string.html) function.


## Body contents {id="body_contents"}
In this chapter, we'll show how Ktor helps to receive various types of body contents send with `POST`, `PUT` or `PATCH` requests.
This can be serialized data (for example, JSON objects), multipart form data, or raw payload.

### Objects {id="objects"}
Ktor provides the [ContentNegotiation](serialization.md) feature to negotiate a media type of a request and deserialize content to an object of a required type. To receive and convert a content for a request, call the [receive](https://api.ktor.io/%ktor_version%/io.ktor.request/receive.html) method that accepts a data class as a parameter: 
```kotlin
post("/customer") {
    val customer = call.receive<Customer>()
}
```
You can learn more from [](serialization.md).

### Form data {id="form_data"}
Ktor allows you to receive form parameters sent with both `application/x-www-form-urlencoded` and `multipart/form-data` types using the [receiveParameters](https://api.ktor.io/%ktor_version%/io.ktor.request/receive-parameters.html) function. The example below shows a sample [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) `POST` request with body as parameters:
```HTTP
POST http://localhost:8080/post
Content-Type: application/x-www-form-urlencoded

first_name=Jet&last_name=Brains
```
In a code snippet below, the `firstName` will be initialized with _Jet_ after sending a request:
```kotlin
post("/post") {
    val firstName = call.receiveParameters()["first_name"]
}
```

If you need to receive a file sent as a part of a multipart request, you can use the [receiveMultipart](https://api.ktor.io/%ktor_version%/io.ktor.request/receive-multipart.html) function. The example below shows how to do this:
```kotlin
```
{src="/snippets/multipart/src/main/kotlin/com/example/Multipart.kt" include-symbol="main"}

Learn how to run this sample from [Multipart](https://github.com/ktorio/ktor-documentation/tree/master/codeSnippets/snippets/multipart).


### Raw payload {id="raw"}
If you need to access the raw body payload and parse it by yourself, you can use the following functions:
* [receiveChannel](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/receive-channel.html)
* [receiveStream](https://api.ktor.io/%ktor_version%/io.ktor.request/receive-stream.html)
* [receiveText](https://api.ktor.io/%ktor_version%/io.ktor.request/receive-text.html)

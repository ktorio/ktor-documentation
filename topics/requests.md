[//]: # (title: Requests)

Ktor allows you to handle incoming requests and send [responses](responses.md) inside [route handlers](Routing_in_Ktor.md#define_route). You can perform various actions when handing requests:
* Get [request information](#request_information), such as headers, cookies, a user agent, and so on.
* Get a [route parameter](#route_parameters) value.
* Obtain parameters of a [query string](#query_parameters).
* Receive [body contents](#body_contents) (raw, form data, data objects, file uploads).

## General request information {id="request_information"}
Get access to request [call.request](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/request.html) and get access to [ApplicationRequest](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/index.html):
```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```
> [call.respondText](responses.md) sends a response back to the client.

Properties and extension functions with other information exposed by [ApplicationRequest](https://api.ktor.io/%ktor_version%/io.ktor.request/-application-request/index.html):
* Headers: `ApplicationRequest.headers` or convenient extension functions: `acceptEncoding`, `contentType`, `cacheControl`, and so on.
* Cookies: `ApplicationRequest.cookies`. To handle sessions using cookies, have a look to the [Sessions](sessions.md) feature.
* HTTP method, HTTP version
* URI and port


## Route parameters {id="route_parameters"}
A _route parameter_ (`{param}`) [matches a path segment](Routing_in_Ktor.md#match_url) and captures it as a parameter named `param`. This path segment is mandatory, but you can make it optional by adding a question mark: `{param?}`. For example:
* `/user/{login}` matches `/user/john`, but doesn't match `/user`.
* `/user/{login?}` matches `/user/john` as well as `/user`.

To access a parameter value inside the route handler, use the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _john_ for the `/user/john` path:
```kotlin
get("/user/{login}") {
  call.respondText("Hello, ${call.parameters["login"]}")
}
```

## Query parameters {id="query_parameters"}
<emphasis tooltip="query_string">query string</emphasis>
If you need to access the query parameters `?param1=value&param2=value` as a collection,
you can use `queryParameters`. It implements the `StringValues` interface where
each key can have a list of Strings associated with it.

```kotlin
val queryParameters: Parameters = request.queryParameters
val param1: String? = request.queryParameters["param1"] // To access a single parameter (first one if repeated)
val repeatedParam: List<String>? = request.queryParameters.getAll("repeatedParam") // Multiple values
```

You can also access the raw `queryString` (`param1=value&param2=value`):

```kotlin
val queryString: String = request.queryString()
```

## Body contents {id="body_contents"}

### Multipart {id="multipart"}
To parse a form urlencoded or with multipart, you can use `receiveParameters` or `receive<Parameters>`:

```kotlin
val postParameters: Parameters = call.receiveParameters()
```

### Objects {id="objects"}
Call the [receive](https://api.ktor.io/%ktor_version%/io.ktor.request/receive.html) method that accepts a data class as a parameter:
```kotlin
post("/customer") {
    val customer = call.receive<Customer>()
}
```
Learn more from [](serialization.md).

### Files {id="files"}

### Raw payload {id="raw"}


## Reverse proxy support: `origin` and `local` {id="info-origin-local"}

When behind a reverse-proxy (for example an nginx or a load balancer), the received request is not performed by the end-user, but that reverse proxy.
That means that the client IP address of the connection would be the one of the proxy instead of the client.
Also the reverse proxy might be serving via HTTPS and requesting to your server via HTTP.
Popular reverse proxies send `X-Forwarded-` headers to be able to access this information. 

>Note that for this to work when under a reverse-proxy you have to install the [XForwardedHeaderSupport](forward-headers.md) feature.
>
{type="note"}

As part of the request object, there are two properties `local` and `origin` that allows to get information of the original request
or the local/proxied one. 

```kotlin
val local : RequestConnectionPoint = request.local // Local information 
val origin: RequestConnectionPoint = request.origin // Local / Origin if XForwardedHeaderSupport feature is installed.
```

The local/origin information you can get:

```kotlin
interface RequestConnectionPoint {
    val scheme: String // "http" or "https": The provided protocol (local) or `X-Forwarded-Proto`
    val version: String // "HTTP/1.1"
    val port: Int
    val host: String // The provided host (local) or `X-Forwarded-Host`
    val uri: String
    val method: HttpMethod
    val remoteHost: String // The client IP (the direct ip for `local`, or the redirected one `X-Forwarded-For`)
}
```
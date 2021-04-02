[//]: # (title: Responses)

Ktor allows you to handle incoming [requests](requests.md) and send responses inside [route handlers](Routing_in_Ktor.md#define_route). You can configure a response in various ways:
* Set a status code.
* Specify a content type.
* Configure headers and cookies.
* Specify the payload: send a plain text, build HTML using Kotlin DSL or choose a JVM template engine, send a serialized data object, and so on. 

## API overview {id="overview"}
Inside a route handler, you can get access to request data using the [call.response](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/response.html) property.

Two ways to control responses:
* The `call.response` providing information about a response. Allows you to set a status code, content type, cookies and headers.
* A set of `call.respond...` methods for sending various [payloads](#payload): text, html, ... Method overloads also allow you to set a status code, content type.
* The `call.respond` method for generic content (ContentNegotiation)

## Set response parameters {id="parameters"}
### Status code {id="status"}
Set using the `ApplicationResponse.status` function:
```kotlin
get("/") {
    call.response.status(HttpStatusCode.OK)
}
```
Custom status code:
```kotlin
get("/") {
    response.status(HttpStatusCode(418, "I'm a tea pot"))
}
```

Or call a required method overload for sending a [payload](#payload).

### Content type {id="content-type"}
Automatically when sending payload.

Or call a required method overload for sending a [payload](#payload).
```kotlin
get("/") {
    call.respondText("Hello, world!", ContentType.Text.Plain, HttpStatusCode.OK)
}
```

### Headers {id="headers"}
The `append` method:
```kotlin
get("/") {
    call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
}
```
Or `header` overloads (custom header):
```kotlin
get("/") {
    call.response.header(HttpHeaders.ETag, "7c876b7e")
}
```
Or convenient functions (`etag`, `link`, ...):
```kotlin
get("/") {
    call.response.etag("Etag value")
}
```
Or custom header:
```kotlin
get("/") {
    call.response.header("Custom", "sfjirjgiu")
}
```

> Default headers, Caching headers, Conditional headers, HSTS

### Cookies {id="cookies"}
```kotlin
get("/") {
    call.response.cookies.append("custom-cookie", "ggg")
}
```
To learn how to handle sessions using cookies, see the [Sessions](sessions.md) section.

## Set response payload {id="payload"}
### Plain text {id="plain-text"}
```kotlin
get("/") {
    call.respondText("Hello, world!")
}
```

### HTML/CSS {id="html"}
[](Working_with_views.md).

Example [HTML DSL](html_dsl.md):
```kotlin
get("/") {
    val name = "Ktor"
    call.respondHtml {
        head {
            title {
                +name
            }
        }
        body {
            h1 {
                +"Hello from $name!"
            }
        }
    }
}
```
Example [FreeMarker](freemarker.md):
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(FreeMarkerContent("index.ftl", mapOf("user" to sampleUser)))
}
```
Or `respondTemplate`:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respondTemplate("index.ftl", mapOf("user" to sampleUser))
}
```


### Object {id="object"}
Enable [ContentNegotiation](serialization.md) feature and sent:
```kotlin
post("/customer") {
    call.respond(Customer(1, "Jet", "Brains"))
}
```


### File {id="file"}
[set headers](#headers) to make downloadable

The `respondFile` example:
```kotlin
```
{src="/snippets/download-file/src/main/kotlin/com/example/DownloadFile.kt" include-symbol="main"}

Learn how to run this sample from [Multipart](https://github.com/ktorio/ktor-documentation/tree/master/codeSnippets/snippets/download-file).

### Raw payload {id="raw"}
If you need to send the raw body payload, you can use the following functions:
* respondBytes
* respondWrite

## Redirects {id="redirect"}
In HTTP, redirection is triggered by a server sending a special redirect response to a request. Redirect responses have status codes that start with 3, and a Location header holding the URL to redirect to.
[HttpsRedirect](https-redirect.md)

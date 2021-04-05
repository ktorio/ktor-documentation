[//]: # (title: Responses)

Ktor allows you to handle incoming [requests](requests.md) and send responses inside [route handlers](Routing_in_Ktor.md#define_route). You can send different types of responses: plain text, HTML documents and templates, serialized data objects, and so on. For each response, you can also configure various [response parameters](#parameters), such as a content type, headers, and cookies.

Inside a route handler, the following API is available for working with responses:
* A set of functions targeted for sending a specific content type, for example, [call.respondText](https://api.ktor.io/%ktor_version%/io.ktor.response/respond-text.html), [call.respondHtml](https://api.ktor.io/%ktor_version%/io.ktor.html/respond-html.html), and so on.
* The [call.respond](https://api.ktor.io/%ktor_version%/io.ktor.response/respond.html) function that allows you to send any data inside a response. For example, with the enabled [ContentNegotiation](serialization.md) feature, you can send a data object serialized in a specific format.
* The [call.response](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/response.html) property that returns the [ApplicationResponse](https://api.ktor.io/%ktor_version%/io.ktor.response/-application-response/index.html) object providing access to response parameters and allowing you to set a status code, add headers, and configure cookies.


## Set response payload {id="payload"}
### Plain text {id="plain-text"}
To send a plain text in a response, use the [call.respondText](https://api.ktor.io/%ktor_version%/io.ktor.response/respond-text.html) function:
```kotlin
get("/") {
    call.respondText("Hello, world!")
}
```

### HTML {id="html"}
Ktor provides two main ways to send HTML responses a to client:
* By building HTML using Kotlin HTML DSL
* By using JVM template engines, such as Freemarker, Velocity, and so on.

To send HTML build using Kotlin DSL, use the [call.respondHtml](https://api.ktor.io/%ktor_version%/io.ktor.html/respond-html.html) function:
```kotlin
```
{src="snippets/html/src/main/kotlin/com/example/Application.kt" lines="12-26"}

If you want to respond with a template, call the [call.respond](https://api.ktor.io/%ktor_version%/io.ktor.response/respond.html) function with a specific content ...
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" lines="16-19"}

... or use an appropriate [call.respondTemplate](https://api.ktor.io/%ktor_version%/io.ktor.freemarker/respond-template.html) function: 
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respondTemplate("index.ftl", mapOf("user" to sampleUser))
}
```
Learn more about working with views from [](Working_with_views.md).


### Object {id="object"}
To enable serialization of data objects in Ktor, you need to install the [ContentNegotiation](serialization.md) feature and register a required converter (for example, JSON). Then, you can use the [call.respond](https://api.ktor.io/%ktor_version%/io.ktor.response/respond.html) function to pass a data object in a response:

```kotlin
post("/customer") {
    call.respond(Customer(1, "Jet", "Brains"))
}
```


### File {id="file"}
To respond to a client with a content of a file, use the [call.respondFile](https://api.ktor.io/%ktor_version%/io.ktor.response/respond-file.html) function. A code snippet below shows how respond with a specified file and make this file downloadable by using the [Content-Disposition](#headers) header:
```kotlin
```
{src="/snippets/download-file/src/main/kotlin/com/example/DownloadFile.kt" include-symbol="main"}

To learn how to run this sample, see [download-file](https://github.com/ktorio/ktor-documentation/tree/master/codeSnippets/snippets/download-file).


### Raw payload {id="raw"}
If you need to send the raw body payload, use the [call.respondBytes](https://api.ktor.io/%ktor_version%/io.ktor.response/respond-bytes.html) function.


## Set response parameters {id="parameters"}
### Status code {id="status"}
To set a status code for a response, call [ApplicationResponse.status](https://api.ktor.io/%ktor_version%/io.ktor.response/-application-response/status.html). You can pass a predefined status code value ...
```kotlin
get("/") {
    call.response.status(HttpStatusCode.OK)
}
```
... or specify a custom status code:
```kotlin
get("/") {
    response.status(HttpStatusCode(418, "I'm a tea pot"))
}
```

Note that functions for sending a [payload](#payload) have overloads for specifying a status code.

### Content type {id="content-type"}
When you send a content using a function targeted for sending a [specific content type](#payload), Ktor specifies a content type automatically. If required, you can specify a content type manually by passing a corresponding parameter. For example, the `call.respondText` function below accepts `ContentType.Text.Plain` as a parameter:
```kotlin
get("/") {
    call.respondText("Hello, world!", ContentType.Text.Plain, HttpStatusCode.OK)
}
```

### Headers {id="headers"}
There are several ways to send specific headers in a response:
* Add a header to the [ApplicationResponse.headers](https://api.ktor.io/%ktor_version%/io.ktor.response/-application-response/headers.html) collection:
   ```kotlin
   get("/") {
       call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
   }
   ```
  
* Call the [ApplicationResponse.header](https://api.ktor.io/%ktor_version%/io.ktor.response/header.html) function:
   ```kotlin
   get("/") {
       call.response.header(HttpHeaders.ETag, "7c876b7e")
   }
   ```
  
* Use functions dedicated to specifying concrete headers, for example, `ApplicationResponse.etag`, `ApplicationResponse.link`, and so on.
   ```kotlin
   get("/") {
       call.response.etag("7c876b7e")
   }
   ```
  
* To add a custom header, pass its name as a string value to any function mentioned above, for example:
   ```kotlin
   get("/") {
       call.response.header("Custom-Header", "Some value")
   }
   ```

> To add the standard `Server` and `Date` headers into each response, install the [DefaultHeaders](default_headers.md) feature.
>
{type="tip"}

### Cookies {id="cookies"}
To access cookies sent in a response, use the [ApplicationResponse.cookies](https://api.ktor.io/%ktor_version%/io.ktor.response/-application-response/cookies.html) property:
```kotlin
get("/") {
    call.response.cookies.append("yummy_cookie", "choco")
}
```
Ktor also provides the capability to handle sessions using cookies. You can learn more from the [Sessions](sessions.md) section.


## Redirects {id="redirect"}
To generate a redirection response, call the [respondRedirect](https://api.ktor.io/%ktor_version%/io.ktor.response/respond-redirect.html) function:
```kotlin
get("/") {
    call.respondRedirect("/moved", permanent = true)
}

get("/moved") {
    call.respondText("Moved content")
}
```

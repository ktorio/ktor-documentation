[//]: # (title: Sending responses)

<show-structure for="chapter" depth="2"/>

<link-summary>Learn how to send different types of responses: plain text, HTML documents and templates, serialized data objects, and so on.</link-summary>

Ktor allows you to handle incoming [requests](requests.md) and send responses inside [route handlers](Routing_in_Ktor.md#define_route). You can send different types of responses: plain text, HTML documents and templates, serialized data objects, and so on. For each response, you can also configure various [response parameters](#parameters), such as a content type, headers, and cookies.

Inside a route handler, the following API is available for working with responses:
* A set of functions targeted for [sending a specific content type](#payload), for example, [call.respondText](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-text.html), [call.respondHtml](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-html-builder/io.ktor.server.html/respond-html.html), and so on. 
* The [call.respond](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond.html) function that allows you to [send any data](#payload) inside a response. For example, with the enabled [ContentNegotiation](serialization.md) plugin, you can send a data object serialized in a specific format.
* The [call.response](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-call/response.html) property that returns the [ApplicationResponse](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/-application-response/index.html) object providing access to [response parameters](#parameters) and allowing you to set a status code, add headers, and configure cookies.
* The [call.respondRedirect](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-redirect.html) provides the capability to add redirects.


## Set response payload {id="payload"}
### Plain text {id="plain-text"}
To send a plain text in a response, use the [call.respondText](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-text.html) function:
```kotlin
get("/") {
    call.respondText("Hello, world!")
}
```

### HTML {id="html"}
Ktor provides two main ways to send HTML responses to a client:
* By building HTML using Kotlin HTML DSL.
* By using JVM template engines, such as FreeMarker, Velocity, and so on.

To send HTML build using Kotlin DSL, use the [call.respondHtml](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-html-builder/io.ktor.server.html/respond-html.html) function:
```kotlin
```
{src="snippets/html/src/main/kotlin/com/example/Application.kt" lines="12-26"}

To send a template in a response, call the [call.respond](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond.html) function with a specific content ...
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" lines="16-19"}

... or use an appropriate [call.respondTemplate](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-freemarker/io.ktor.server.freemarker/respond-template.html) function: 
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respondTemplate("index.ftl", mapOf("user" to sampleUser))
}
```
You can learn more from the [](Working_with_views.md) help section.


### Object {id="object"}
To enable serialization of data objects in Ktor, you need to install the [ContentNegotiation](serialization.md) plugin and register a required converter (for example, JSON). Then, you can use the [call.respond](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond.html) function to pass a data object in a response:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="32-36"}

You can find the full example here: [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).


### File {id="file"}
To respond to a client with a contents of a file, use the [call.respondFile](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-file.html) function. A code sample below shows how to send a specified file in a response and make this file downloadable by adding the `Content-Disposition` [header](#headers):
```kotlin
```
{src="/snippets/download-file/src/main/kotlin/com/example/DownloadFile.kt" include-symbol="main"}

Note that this sample has two plugins installed:
- [PartialContent](partial-content.md) enables the server to respond to requests with the `Range` header and send only a portion of content.
- [AutoHeadResponse](autoheadresponse.md) provides the ability to automatically respond to `HEAD` request for every route that has a `GET` defined. This allows the client application to determine the file size by reading the `Content-Length` header value.

To learn how to run the sample, see [download-file](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/download-file).


### Raw payload {id="raw"}
If you need to send the raw body payload, use the [call.respondBytes](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-bytes.html) function.


## Set response parameters {id="parameters"}
### Status code {id="status"}
To set a status code for a response, call [ApplicationResponse.status](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/-application-response/status.html). You can pass a predefined status code value ...
```kotlin
get("/") {
    call.response.status(HttpStatusCode.OK)
}
```
... or specify a custom status code:
```kotlin
get("/") {
    call.response.status(HttpStatusCode(418, "I'm a tea pot"))
}
```

Note that functions for sending a [payload](#payload) have overloads for specifying a status code.

### Content type {id="content-type"}
With the installed [ContentNegotiation](serialization.md) plugin, Ktor chooses a content type for a [response](#payload) automatically. If required, you can specify a content type manually by passing a corresponding parameter. For example, the `call.respondText` function in a code snippet below accepts `ContentType.Text.Plain` as a parameter:
```kotlin
get("/") {
    call.respondText("Hello, world!", ContentType.Text.Plain, HttpStatusCode.OK)
}
```

### Headers {id="headers"}
There are several ways to send specific headers in a response:
* Add a header to the [ApplicationResponse.headers](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/-application-response/headers.html) collection:
   ```kotlin
   get("/") {
       call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
   }
   ```
  
* Call the [ApplicationResponse.header](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/header.html) function:
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

> To add the standard `Server` and `Date` headers into each response, install the [DefaultHeaders](default_headers.md) plugin.
>
{type="tip"}

### Cookies {id="cookies"}
To access cookies sent in a response, use the [ApplicationResponse.cookies](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/-application-response/cookies.html) property:
```kotlin
get("/") {
    call.response.cookies.append("yummy_cookie", "choco")
}
```
Ktor also provides the capability to handle sessions using cookies. You can learn more from the [Sessions](sessions.md) section.


## Redirects {id="redirect"}
To generate a redirection response, call the [respondRedirect](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.response/respond-redirect.html) function:
```kotlin
get("/") {
    call.respondRedirect("/moved", permanent = true)
}

get("/moved") {
    call.respondText("Moved content")
}
```

[//]: # (title: Sending responses)

<show-structure for="chapter" depth="2"/>

<link-summary>Learn how to send different types of responses.</link-summary>

Ktor allows you to handle incoming [requests](server-requests.md) and send responses inside [route handlers](server-routing.md#define_route). You can send 
different types of responses: plain text, HTML documents and templates, serialized data objects, and so on. You can also
configure various [response parameters](#parameters), such as content type, headers, cookies, and the status code.

Inside a route handler, the following API is available for working with responses:
* A set of functions for [sending specific content types](#payload), such as [`call.respondText()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-text.html) and [`call.respondHtml()`](https://api.ktor.io/ktor-server-html-builder/io.ktor.server.html/respond-html.html). 
* The [`call.respond()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond.html) function that allows you to [send any data type](#payload) inside a response. When the [ContentNegotiation](server-serialization.md) plugin is installed, you can send a data object serialized in a specific format.
* The [`call.response()`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call/response.html) property that returns the [`ApplicationResponse`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/-application-response/index.html) object, providing access to [response parameters](#parameters) for setting the status code, adding headers, and configuring cookies.
* The [`call.respondRedirect()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-redirect.html) function for sending redirect responses.


## Set response payload {id="payload"}

### Plain text {id="plain-text"}

To send plain text, use the [`call.respondText()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-text.html) function:
```kotlin
get("/") {
    call.respondText("Hello, world!")
}
```

### HTML {id="html"}

Ktor provides two main mechanisms for generating HTML responses:
* Building HTML using the Kotlin HTML DSL.
* Rendering templates using JVM template engines such as FreeMarker or Velocity.

#### Full HTML documents

To send HTML built using Kotlin DSL, use the [`call.respondHtml()`](https://api.ktor.io/ktor-server-html-builder/io.ktor.server.html/respond-html.html) function:

```kotlin
```
{src="snippets/html/src/main/kotlin/com/example/Application.kt" include-lines="13-27"}

#### Partial HTML fragments

If you need to return only a fragment of HTML, without wrapping it in `<html>`, `<head>`, or `<body>`, you can use
`call.respondHtmlFragment()`:

```kotlin
```
{src="snippets/html/src/main/kotlin/com/example/Application.kt" include-lines="28-35"}

#### Templates

To send a template in a response, use the [`call.respond()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond.html) function with a specific content:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" include-lines="16-19"}

You can also use the [`call.respondTemplate()`](https://api.ktor.io/ktor-server-freemarker/io.ktor.server.freemarker/respond-template.html) function:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respondTemplate("index.ftl", mapOf("user" to sampleUser))
}
```
You can learn more from the [](server-templating.md) help section.


### Object {id="object"}

To enable serialization of data objects in Ktor, you need to install the [ContentNegotiation](server-serialization.md)
plugin and register a required converter (for example, JSON). Then, you can use the [`call.respond()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond.html)
function to pass a data object in a response:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="32-36"}

For the full example, see [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).

[//]: # (TODO: Check link for LocalPathFile)

### File {id="file"}

To respond to a client with the content of a file, you have two options:

- For `File` resources, use
  the [`call.respondFile()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-file.html)
  function.
- For `Path` resources, use the `call.respond()` function with
  the [`LocalPathContent`](https://api.ktor.io/ktor-server-core/io.ktor.server.http.content/-local-path-content/index.html)
  class.

The example below shows how to send a file and make it downloadable by adding the `Content-Disposition` [header](#headers):

```kotlin
```
{src="snippets/download-file/src/main/kotlin/com/example/DownloadFile.kt" include-lines="3-35"}

Note that this sample uses two plugins:
- [PartialContent](server-partial-content.md) enables the server to respond to requests with the `Range` header and send only a portion of content.
- [AutoHeadResponse](server-autoheadresponse.md) provides the ability to automatically respond to a ` HEAD ` request for every route that has a `GET` defined. This allows the client application to determine the file size by reading the `Content-Length` header value.

For the full code sample,
see [download-file](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/download-file).


### Raw payload {id="raw"}

To send the raw body payload, use the [`call.respondBytes()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-bytes.html) function.


## Set response parameters {id="parameters"}

### Status code {id="status"}

To set a status code for a response, call [`ApplicationResponse.status()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/-application-response/status.html) with a predefined status code value:

```kotlin
get("/") {
    call.response.status(HttpStatusCode.OK)
}
```

You can also specify custom status values:

```kotlin
get("/") {
    call.response.status(HttpStatusCode(418, "I'm a tea pot"))
}
```

> All payload-sending functions also provide overloads that accept a status code.
> 
{style="note"}

### Content type {id="content-type"}

With the [ContentNegotiation](server-serialization.md) plugin installed, Ktor chooses a content type automatically. If required, you can
specify a content type manually by passing a corresponding parameter. 

In the example below, the `call.respondText()` function accepts `ContentType.Text.Plain` as a parameter:

```kotlin
get("/") {
    call.respondText("Hello, world!", ContentType.Text.Plain, HttpStatusCode.OK)
}
```

### Headers {id="headers"}

You can add headers to a response in several ways:
* Modify the [`ApplicationResponse.headers`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/-application-response/headers.html) collection:
   ```kotlin
   get("/") {
       call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
   }
   ```
  
* Use the [`ApplicationResponse.header()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/header.html) function:
   ```kotlin
   get("/") {
       call.response.header(HttpHeaders.ETag, "7c876b7e")
   }
   ```
  
* Use convenience functions for specific headers, such as `ApplicationResponse.etag`, `ApplicationResponse.link`, and others.
   ```kotlin
   get("/") {
       call.response.etag("7c876b7e")
   }
   ```
  
* Add custom headers by passing raw string names:
   ```kotlin
   get("/") {
       call.response.header("Custom-Header", "Some value")
   }
   ```

> To include standard `Server` and `Date` headers automatically, install the [DefaultHeaders](server-default-headers.md) plugin.
>
{type="tip"}

### Cookies {id="cookies"}

To configure cookies sent in a response, use the [`ApplicationResponse.cookies`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/-application-response/cookies.html) property:
```kotlin
get("/") {
    call.response.cookies.append("yummy_cookie", "choco")
}
```

> Ktor also provides the ability to handle sessions using cookies. To learn more, see [](server-sessions.md).


## Redirects {id="redirect"}

To generate a redirect response, use the [`call.respondRedirect()`](https://api.ktor.io/ktor-server-core/io.ktor.server.response/respond-redirect.html) function:

```kotlin
get("/") {
    call.respondRedirect("/moved", permanent = true)
}

get("/moved") {
    call.respondText("Moved content")
}
```

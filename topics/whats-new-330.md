[//]: # (title: What's new in Ktor 3.3.0)

<show-structure for="chapter,procedure" depth="2"/>

## Ktor Server

### Custom fallback for static resources

Ktor 3.3.0 introduces a new `fallback()` function for static content, allowing you to define custom
behavior when a requested resource is not found.

Unlike `default()`, which always serves the same fallback file, `fallback()` gives you access to the original requested
path and the current `ApplicationCall`. You can use this to redirect, return custom status codes, or serve different
files dynamically.

To define custom fallback behaviour, use the `fallback()` function within `staticFiles()`, `staticResources()`, `staticZip()`, or
`staticFileSystem()`:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="22,25-32,45"}

### Development mode auto-reload limitations

In Ktor 3.2.0, the introduced [support for suspend module functions](whats-new-320.md#suspendable-module-functions)
caused a regression where auto-reload stopped working for applications that use blocking module references.

This regression remains in 3.3.0 and auto-reload continues to work only with `suspend` function modules and
configuration references.

Examples of supported module declarations:

```kotlin
// Supported — suspend function reference
embeddedServer(Netty, port = 8080, module = Application::mySuspendModule)

// Supported — configuration reference (application.conf / application.yaml)
ktor {
    application {
        modules = [ com.example.ApplicationKt.mySuspendModule ]
    }
}
```

We plan to restore support for blocking function references in a future release. Until then, prefer a `suspend` module
or configuration reference in `development` mode.

## Ktor Client

### SSE response body buffer

Until now, attempting to call `response.bodyAsText()` after an SSE error failed due to double-consume issues.

Ktor 3.3.0 introduces a configurable diagnostic buffer that allows you to capture already-processed SSE data for
debugging and error handling.

You can configure the buffer globally when installing the [SSE plugin](client-server-sent-events.topic):

```kotlin
install(SSE) {
    bufferPolicy = SSEBufferPolicy.LastEvents(10)
}
```

Or per call:

```kotlin
client.sse(url, { bufferPolicy(SSEBufferPolicy.All) }) {
    // …
}
```

As the SSE stream is consumed, the client maintains a snapshot of the processed data in an in-memory buffer (without
re-reading from the network). If an error occurs, you can safely call `response?.bodyAsText()` for logging or
diagnostics.

For more information, see [Response buffering](client-server-sent-events.topic#response-buffering).

### Updated OkHttp version

In Ktor 3.3.0, the Ktor client's `OkHttp` engine has been upgraded to use OkHttp 5.1.0 (previously 4.12.0). This major
version bump may introduce API changes for projects that interact directly with OkHttp. Such projects should verify
compatibility.

## Shared

### OpenAPI specification generation
<secondary-label ref="experimental"/>

Ktor 3.3.0 introduces an experimental OpenAPI generation feature via the Gradle plugin and a compiler plugin. This
allows you to generate OpenAPI specifications directly from your application code at build time.

It provides the following capabilities:
- Analyze Ktor route definitions and merge nested routes, local extensions, and resource paths.
- Parse preceding KDoc annotations to supply OpenAPI metadata, including:
    - Path, query, header, cookie, and body parameters
    - Response codes and types
    - Security, descriptions, deprecations, and external documentation links
- Infer request and response bodies from `call.receive()` and `call.respond()`.

#### Annotating routes

KDoc tags are used to describe endpoints:

```kotlin
/**
 * Get a single user.
 *
 * @path id The ID of the user
 * @response 404 The user was not found
 * @response 200 [User] The user.
 */
get("/api/users/{id}") {
    val user = repository.get(call.parameters["id"]!!)
        ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respond(user)
}

```
Supported KDoc tags include `@path`, `@query`, `@header`, `@cookie`, `@body`, `@response`, `@deprecated`,
` @description`, `@security`, `@externalDocs`, and `@ignore`.

#### Configuring the Gradle plugin

```kotlin
import io.ktor.plugin.*

plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

application.mainClass = "io.ktor.samples.openapi.ApplicationKt"

ktor {
    @OptIn(OpenApiPreview::class)
    openApi {
        title = "OpenAPI example"
        version = "2.1"
        summary = "This is a sample API"
    }
}
```

#### Generating the OpenAPI specification

To generate the OpenAPI specification file (for example, at
<path>openapi/generated.json</path>
) from your Ktor routes and KDoc annotations, use the following command:

```shell
./gradlew buildOpenApi
```

### Updated Jetty version

The Jetty server and client engines have been upgraded to use Jetty 12. For most applications, this upgrade is fully
backward-compatible, but client and server code now leverage the updated Jetty APIs internally.

If your project uses Jetty APIs directly, be aware that there are breaking changes. For more details, refer to
[the official Jetty migration guide](https://jetty.org/docs/jetty/12.1/programming-guide/migration/11-to-12.html).

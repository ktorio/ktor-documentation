[//]: # (title: What's new in Ktor 3.6.0)

<show-structure for="chapter,procedure" depth="2"/>

[//]: # (TODO: Ensure release date is correct)
_[Released: August 26, 2026](releases.md#release-details)_

Ktor 3.6.0 delivers a range of improvements across server and client. Highlights of this feature release include:

[//]: # (TODO: Add a bullet list with highlights)

## Ktor Server

### Additional type support for request parameters

Ktor 3.6.0 expands the set of types supported by default when converting request parameters to typed values.

The following types are now supported:

* `Uuid`
* `Byte`
* `java.lang.Byte`
* `UByte`
* `UShort`
* `UInt`
* `ULong`

For example, you can retrieve a `Uuid` parameter directly inside a route handler through property delegation:

```kotlin
get {
    val uuid: Uuid by call.parameters
}
```

### OpenAPI tag descriptions

You can now define descriptions for OpenAPI tags directly in the [`openAPI {}`](server-openapi.md) and [`swaggerUI {}`](server-swagger-ui.md)
configuration blocks:

```kotlin
swaggerUI("/swagger") {
    info = OpenApiInfo("Books API from routes", "1.0.0")
    tag(
        name = "Books",
        description = "Operations on books"
    )
}
```

The tag description is added to the top-level metadata of the generated OpenAPI document.

### New `ApplicationCall.respondHtmlPartial()` function

The new `.respondHtmlPartial()` function replaces `.respondHtmlFragment()` for responding
with partial HTML content.

It uses `TagConsumer<Appendable>` as the lambda receiver, which allows you to return unrestricted HTML content,
such as table cells:

```kotlin
call.respondHtmlPartial(HttpStatusCode.Created) {
    td { +"Created!" } 
}
```

The previous `.respondHtmlFragment()` function uses `FlowContent`, which restricts the HTML elements that can be returned.
It is now deprecated in favor of `.respondHtmlPartial()`.

## Ktor Client

### Default client engines for multiplatform projects

Ktor 3.6.0 introduces the `ktor-client-engine-defaults` artifact, which provides a curated set of HTTP [client engines](client-engines.md)
for [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/get-started.html) projects.

Add the dependency to the `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("io.ktor:ktor-client-engine-defaults:3.6.0")
            }
        }
    }
}
```

You can then create an `HttpClient` without specifying an engine:

```kotlin
val client = HttpClient()
```

For each target platform, Ktor uses the default engine provided by `ktor-client-engine-defaults`. This removes the
need to declare separate engine dependencies for each platform in most multiplatform projects.

If your multiplatform project currently uses `CIO` across all supported targets, consider replacing the `CIO` dependency
with `ktor-client-engine-defaults`. This lets Ktor provide a curated default engine for each platform while keeping
engine selection out of your common source set.

You can still [declare a specific client engine](client-dependencies.md#kmp-specific-engine) when you need engine-specific
configuration or behavior.

### WebRTC client support for JVM
<primary-label ref="experimental"/>

The experimental [WebRTC client](client-webrtc.md) now supports JVM desktop applications.

The JVM implementation uses [webrtc-java](https://github.com/devopvoid/webrtc-java) native WebRTC bindings and provides
support for peer connections, audio and video tracks, data channels, and connection statistics.

JVM support currently has several platform-specific limitations. For more information, see the
[WebRTC client](client-webrtc.md) documentation.
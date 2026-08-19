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
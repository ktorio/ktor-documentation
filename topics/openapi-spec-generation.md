[//]: # (title: OpenAPI specification generation)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>
<secondary-label ref="server-feature"/>

<tldr>
<p>
<b>Code example</b>: 
<a href="https://github.com/ktorio/ktor-samples/tree/main/openapi">openapi</a>
</p>
</tldr>

Ktor provides experimental support for generating OpenAPI specifications directly from your Kotlin code.
This functionality is available via the Ktor Gradle plugin and can be combined with the [OpenAPI](server-openapi.md)
and [SwaggerUI](server-swagger-ui.md) plugins to serve interactive API documentation.

> The OpenAPI Gradle extension requires Kotlin 2.2.20. Using other versions may result in compilation
> errors.
>
{style="note"}

## Add the Gradle plugin

To enable specification generation, apply the Ktor Gradle plugin to your project:

```kotlin
plugins {
    id("io.ktor.plugin") version "%ktor_version%"
}
```

## Configure the extension

To configure the extension, use the `openApi` block inside the `ktor` extension in your
<path>build.gradle.kts</path>
file. You can provide metadata such as title, description, license, and contact information:

```kotlin
ktor {
    @OptIn(OpenApiPreview::class)
    openApi {
        title = "OpenAPI example"
        version = "2.1"
        summary = "This is a sample API"
        description = "This is a longer description"
        termsOfService = "https://example.com/terms/"
        contact = "contact@example.com"
        license = "Apache/1.0"

        // Location of the generated specification (defaults to openapi/generated.json)
        target = project.layout.buildDirectory.file("open-api.json")
    }
}
```

## Routing API introspection

The plugin can analyze your server routing DSL to infer basic path information, such as:

- The merged path (`/api/v1/users/{id}`).
- Path parameters.
- HTTP methods (such as `GET` and `POST`).

```kotlin
routing {
    route("/api/v1") {
        get("/users") { }
        get("/users/{id}") { }
        post("/users") { }
    }
}
```

Because request parameters and responses are handled inside route lambdas, the plugin cannot infer detailed
request/response schemas automatically. To generate a complete and useful specification, you can use annotations.

## Annotate routes

To enrich the specification, Ktor uses a KDoc-like annotation API. Annotations provide metadata that cannot be inferred
from code and integrate seamlessly with existing routes.

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

### Supported KDoc fields

| Tag             | Format                                          | Description                                     |
|-----------------|-------------------------------------------------|-------------------------------------------------|
| `@tags`         | `@tags *name`                                   | Associates the endpoint with a tag for grouping |
| `@path`         | `@path [Type] name description`                 | Describes a path parameter                      |
| `@query`        | `@query [Type] name description`                | Query parameter                                 |
| `@header`       | `@header [Type] name description`               | Header parameter                                |
| `@cookie`       | `@cookie [Type] name description`               | Cookie parameter                                |
| `@body`         | `@body contentType [Type] description`          | Request body                                    |
| `@response`     | `@response code contentType [Type] description` | Response with optional type                     |
| `@deprecated`   | `@deprecated reason`                            | Marks endpoint deprecated                       |
| `@description`  | `@description text`                             | Extended description                            |
| `@security`     | `@security scheme`                              | Security requirements                           |
| `@externalDocs` | `@external href`                                | External documentation links                    |


## Generate the specification

To generate the OpenAPI specification, run the following Gradle task:

```shell
./gradlew buildOpenApi
```

This task runs the Kotlin compiler with a custom plugin that analyzes your routing code and produces a
JSON specification.

> Some constructs cannot be evaluated at compile time. The generated specification may be incomplete. Improvements are
> planned for later Ktor releases.
>
{style="note"}

## Serve the specification

To make the generated specification available at runtime, you can use the [OpenAPI](server-openapi.md)
or [SwaggerUI](server-swagger-ui.md) plugins.

The following example serves the generated specification file at an OpenAPI endpoint:

```kotlin
routing {
    openAPI("/docs", swaggerFile = "openapi/generated.json")
}
```
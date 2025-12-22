[//]: # (title: OpenAPI specification generation)

<show-structure for="chapter" depth="2"/>
<secondary-label ref="server-feature"/>

<tldr>
<p>
<b>Code example</b>: 
<a href="https://github.com/ktorio/ktor-samples/tree/main/openapi">openapi</a>
</p>
</tldr>

Ktor provides support for building OpenAPI specifications at runtime from one or more documentation sources.

This functionality is available through:
- The OpenAPI compiler extension (included in the Ktor Gradle plugin), which analyzes routing code at compile time and
contributes metadata.
- The routing annotation runtime API, which assembles a complete OpenAPI specification from the running application.

You can use one, or both, and combine them with the [OpenAPI](server-openapi.md)
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
file:

```kotlin
ktor {
    openApi {
        // Global control for the compiler plugin
        enabled = true
        
        // Enables / disables inferring details from call handler code
        codeInferenceEnabled = true
        
        // Toggles whether analysis should be applied to all routes or only those which are commented
        onlyCommented = false
    }
}
```

## Routing API introspection

The plugin analyzes your server routing DSL to infer basic path information, such as:

- Merged paths (for example, `/api/v1/users/{id}`).
- HTTP methods (such as `GET` and `POST`).
- Path parameters.

```kotlin
routing {
    route("/api/v1") {
        get("/users") { }
        get("/users/{id}") { }
        post("/users") { }
    }
}
```

Because request parameters and responses are handled inside route lambdas, the compiler alone cannot infer detailed
request/response schemas automatically. To generate a complete and useful specification, you can use annotations.

## Annotate routes

To enrich the specification, Ktor supports two ways of annotating routes:

- [KDoc-based annotations](#kdoc-annotations), analyzed by the compiler plugin.
- [Runtime route annotations](#runtime-route-annotations) using the `.annotate {}` DSL.

### KDoc annotations {id="kdoc-annotations"}

KDoc-style annotations provide metadata that cannot be inferred from code and integrate seamlessly with existing
routes.

You can attach them directly to route declarations:

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

#### Supported KDoc fields

| Tag             | Format                                          | Description                                     |
|-----------------|-------------------------------------------------|-------------------------------------------------|
| `@tag`          | `@tag *name`                                    | Associates the endpoint with a tag for grouping |
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


### Runtime route annotations {id="runtime-route-annotations"}

For cases where compile-time analysis is insufficient – such as dynamic routing, interceptors, or conditional logic –
you can attach OpenAPI metadata directly to a route using the `.annotate {}` DSL:

```kotlin
get("/messages") {
    val query = call.parameters["q"]?.let(::parseQuery)
    call.respond(messageRepository.getMessages(query))
}.annotate {
    parameters {
        query("q") {
            description = "An encoded query"
            required = false
        }
    }
    responses {
        HttpStatusCode.OK {
            description = "A list of messages"
            schema = jsonSchema<List<Message>>()
            extension("x-sample-message", testMessage)
        }
        HttpStatusCode.BadRequest {
            description = "Invalid query"
            ContentType.Text.Plain()
        }
    }
    summary = "get messages"
    description = "Retrieves a list of messages."
}
```

When both KDoc annotations and runtime annotations are present, runtime annotations take precedence.

## Generate the specification

To generate the OpenAPI specification, use `generateOpenApiSpec` from your application:

```kotlin
val spec = generateOpenApiSpec(
    info = OpenApiInfo(
        title = "Example API",
        version = "2.1",
        description = "This is a sample API"
    ),
    route = application.routingRoot
)

```

This function assembles a complete OpenAPI document using:

- Compiler-generated metadata.
- Runtime route annotations.
- Schema inference via `kotlinx.serialization`.

## Serve the specification

To make the generated specification available at runtime, you can use the [OpenAPI](server-openapi.md)
or [SwaggerUI](server-swagger-ui.md) plugins.

The following example serves the generated specification file at an OpenAPI endpoint:

```kotlin
routing {
    openAPI("/docs", swaggerFile = "openapi/generated.json")
}
```
[//]: # (title: OpenAPI specification generation)

<show-structure for="chapter" depth="2"/>
<secondary-label ref="server-feature"/>

<var name="artifact_name" value="ktor-server-routing-annotate"/>
<var name="package_name" value="io.ktor.server.routing.annotate"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p>
<b>Code example</b>: 
<a href="https://github.com/ktorio/ktor-samples/tree/main/openapi">openapi</a>
</p>
</tldr>

Ktor provides support for building OpenAPI specifications at runtime from one or more documentation sources.

This functionality is available through:
- The OpenAPI compiler extension (included in the Ktor Gradle plugin), which analyzes routing code at compile time and
generates metadata.
- The routing annotation runtime API, which assembles a complete OpenAPI specification from the running application.

You can use one, or both, and combine them with the [OpenAPI](server-openapi.md)
and [SwaggerUI](server-swagger-ui.md) plugins to serve interactive API documentation.

> The OpenAPI Gradle extension requires Kotlin 2.2.20. Using other versions may result in compilation
> errors.
>
{style="note"}

## Add dependencies

* To enable specification generation, apply the Ktor Gradle plugin to your project:

```kotlin
plugins {
    id("io.ktor.plugin") version "%ktor_version%"
}
```

* To use runtime route annotations, add the `%artifact_name%` artifact in your build script:

  <include from="lib.topic" element-id="add_ktor_artifact"/>

## Configure the extension

To configure the plugin extension, use the `openApi` block inside the `ktor` extension in your
<path>build.gradle.kts</path>
file:

[//]: # (TODO: reference line numbers from example project)
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

The Ktor compiler plugin analyzes your server routing DSL to infer basic path information, such as:

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
```
{src="snippets/openapi-spec-gen/src/main/kotlin/com/example/Application.kt" include-lines="37-51"}

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
```
{src="snippets/openapi-spec-gen/src/main/kotlin/com/example/Application.kt" include-lines="58-86"}

When both KDoc annotations and runtime annotations are present, runtime annotations take precedence.

## Generate the specification

To generate the OpenAPI specification, use the `generateOpenApiDoc()` function:

[//]: # (TODO: reference line numbers from example project)
```kotlin
val doc = generateOpenApiDoc(
    base = ...
    routes = ...
)
```

This function assembles a complete OpenAPI document using the compiler-generated metadata and runtime route annotations.

## Serve the specification

To make the generated specification available at runtime, you can use the [OpenAPI](server-openapi.md)
or [SwaggerUI](server-swagger-ui.md) plugins.

The following example serves the generated specification file at an OpenAPI endpoint:

```kotlin
routing {
    openAPI("/docs", swaggerFile = "openapi/generated.json")
}
```
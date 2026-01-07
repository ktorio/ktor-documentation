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

## Configure the OpenAPI compiler extension

The OpenAPI compiler extension controls how routing metadata is collected and generated at compile time.
It does not define the final OpenAPI document itself.

General OpenAPI information—such as the API title, version, servers, security schemes, and detailed schemas—is supplied
at runtime [when the specification is generated](#generate-and-serve-the-specification).

To configure the compiler plugin extension, use the `openApi` block inside the `ktor` extension in your
<path>build.gradle.kts</path>
file:

[//]: # (TODO: reference line numbers from example project)
```kotlin
ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}
```

### Configuration options

<deflist>
<def>
<title><code>enabled</code></title>
Enables or disables generation of OpenAPI metadata during compilation.
</def>
<def>
<title><code>codeInferenceEnabled</code></title>
Controls whether the compiler attempts to infer OpenAPI metadata from routing code. Disable this option if inference
produces incorrect results or if you prefer to define metadata exclusively through annotations.
</def>
<def>
<title><code>onlyCommented</code></title>
Limits processing to routes that contain KDoc comments. By default, all routes are analyzed.
</def>
</deflist>

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
- [Runtime route annotations](#runtime-route-annotations), defined using the `.annotate {}` DSL.

You can use either approach or combine both.

### KDoc annotations {id="kdoc-annotations"}

KDoc-style annotations provide metadata that cannot be inferred from code and integrate seamlessly with existing
routes.

You can attach them directly to route declarations:

[//]: # (TODO: reference line numbers from example project)

```kotlin
/**
 * Get a single user by ID.
 *
 * @path id [ULong] the ID of the user
 * @response 400 The ID parameter is malformatted or missing.
 * @response 404 The user for the given ID does not exist.
 * @response 200 [User] The user found with the given ID.
*/
get("/{id}") {
    val id = call.parameters["id"]?.toULongOrNull()
        ?: return@get call.respond(HttpStatusCode.BadRequest)
    val user = list.find { it.id == id }
        ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respond(user)
}
```

#### Supported KDoc fields

| Tag             | Format                                          | Description                      |
|-----------------|-------------------------------------------------|----------------------------------|
| `@tag`          | `@tag *name`                                    | Groups the endpoint under a tag  |
| `@path`         | `@path [Type] name description`                 | Path parameter                   |
| `@query`        | `@query [Type] name description`                | Query parameter                  |
| `@header`       | `@header [Type] name description`               | Header parameter                 |
| `@cookie`       | `@cookie [Type] name description`               | Cookie parameter                 |
| `@body`         | `@body contentType [Type] description`          | Request body                     |
| `@response`     | `@response code contentType [Type] description` | Response definition              |
| `@deprecated`   | `@deprecated reason`                            | Marks the endpoint as deprecated |
| `@description`  | `@description text`                             | Extended description             |
| `@security`     | `@security scheme`                              | Security requirements            |
| `@externalDocs` | `@external href`                                | External documentation link      |


### Runtime route annotations {id="runtime-route-annotations"}

For cases where compile-time analysis is insufficient – such as dynamic routing, interceptors, or conditional logic –
you can attach OpenAPI metadata directly to a route using the `.annotate {}` DSL:

[//]: # (TODO: reference line numbers from example project)

```kotlin
get("/users") {
    val query = call.parameters["q"]
    val result = if (query != null) {
        list.filter {it.name.contains(query, ignoreCase = true)  }
    } else {
        list
    }

    call.respond(result)
}.annotate {
    summary = "Get users"
    description = "Retrieves a list of users."
    parameters {
        query("q") {
            description = "An encoded query"
            required = false
        }
    }
    responses {
        HttpStatusCode.OK {
            description = "A list of users"
            schema = jsonSchema<List<User>>()
        }
        HttpStatusCode.BadRequest {
            description = "Invalid query"
            ContentType.Text.Plain()
        }
    }
}
```

When both KDoc annotations and runtime annotations are present, runtime annotations take precedence.

## Generate and serve the specification

To generate and serve the OpenAPI specification, you have the following options:

- Use the `generateOpenApiDoc()` function to serve the generated specification file.
- Use the [OpenAPI](server-openapi.md) or [SwaggerUI](server-swagger-ui.md) plugins to serve interactive API documentation.

### Generate and serve the specification file

To assemble a complete OpenAPI document using the compiler-generated metadata and runtime route annotations, use the
`generateOpenApiDoc()` function directly in your route:

[//]: # (TODO: reference line numbers from example project)
```kotlin
get("/docs.json") {
    val docs = generateOpenApiDoc(
        OpenApiDoc(info = OpenApiInfo("My API", "1.0")),
        apiRoute.descendants()
    )
    call.respond(docs)
}
```

### Serve interactive specification

To expose the specification using a UI, use the [OpenAPI](server-openapi.md)
and [SwaggerUI](server-swagger-ui.md) plugins:

```kotlin
// Serves the OpenAPI UI
openAPI("/openApi")

// Serves the Swagger UI
swaggerUI("/swaggerUI") {
    info = OpenApiInfo("My API", "1.0")
    source = OpenApiDocSource.RoutingSource(ContentType.Application.Json) {
        apiRoute.descendants()
    }
}
```
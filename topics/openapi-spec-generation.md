[//]: # (title: OpenAPI specification generation)

<show-structure for="chapter" depth="2"/>
<secondary-label ref="server-feature"/>

<var name="artifact_name" value="ktor-server-routing-openapi"/>
<var name="package_name" value="io.ktor.server.routing.openapi"/>

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
* The OpenAPI compiler extension (included in the Ktor Gradle plugin), which analyzes routing code at compile time and
generates Kotlin code that registers OpenAPI metadata at runtime.
* The routing annotation runtime API, which attaches OpenAPI metadata directly to routes in the running application.

You can use one or both and combine them with the [OpenAPI](server-openapi.md)
and [SwaggerUI](server-swagger-ui.md) plugins to serve interactive API documentation.

> The OpenAPI Gradle extension requires Kotlin 2.2.20. Using other versions may result in compilation
> errors.
>
{style="note"}

## Add dependencies

* To enable OpenAPI metadata generation, apply the Ktor Gradle plugin to your project:

```kotlin
plugins {
    id("io.ktor.plugin") version "%ktor_version%"
}
```

* To use runtime route annotations, add the `%artifact_name%` artifact to your build script:

  <include from="lib.topic" element-id="add_ktor_artifact"/>

## Configure the OpenAPI compiler extension {id="configure-the-extension"}

The OpenAPI compiler extension controls how routing metadata is collected at compile time.
It does not define the final OpenAPI document itself.

During compilation, the plugin generates Kotlin code that uses the OpenAPI runtime API to register metadata derived
from routing declarations, code patterns, and comments.

General OpenAPI information — such as the API title, version, servers, security schemes, and detailed schemas — is supplied
at runtime [when the specification is generated](#generate-and-serve-the-specification).

To configure the compiler plugin extension, use the `openApi {}` block inside the `ktor` extension in your
<path>build.gradle.kts</path>
file:

```kotlin
```
{src="snippets/openapi-spec-gen/build.gradle.kts" include-lines="20-26"}

### Configuration options

<deflist>
<def>
<title><code>enabled</code></title>
Enables or disables OpenAPI route annotation code generation. Defaults to <code>false</code>.
</def>
<def>
<title><code>codeInferenceEnabled</code></title>
Controls whether the compiler attempts to infer OpenAPI metadata from routing code. Defaults to <code>true</code>. 
Disable this option if inference produces incorrect results, or you prefer to define metadata explicitly using
annotations.
For more details, see <a href="#code-inference">code inference rules</a>.
</def>
<def>
<title><code>onlyCommented</code></title>
Limits metadata generation to routes that contain comment annotations. Defaults to <code>false</code>, meaning all
routing calls are processed except those explicitly marked with <code>@ignore</code>.
</def>
</deflist>

### Routing structure analysis

The Ktor compiler plugin analyzes your server routing DSL to determine the structural shape of your API. This analysis
is based solely on route declarations and does not inspect the contents of route handlers.

The following is automatically inferred from the selectors in the routing API tree:
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

Because request parameters, bodies, and responses are handled inside route lambdas, the compiler cannot infer a complete
OpenAPI description from the routing structure alone. To enrich the generated metadata, Ktor supports
[annotations](#annotate-routes) and [automatic inference](#code-inference) based on common request-handling patterns.

### Code inference

When code inference is enabled, the compiler plugin recognizes common Ktor usage patterns and generates
equivalent runtime annotations automatically.

The following table summarizes the supported inference rules:

| Rule                | Description                                                    | Input                                                                      | Output (from annotate scope)                                             |
|---------------------|----------------------------------------------------------------|----------------------------------------------------------------------------|--------------------------------------------------------------------------|
| Request Body        | Provides request body schema from `ContentNegotiation` reads   | `call.receive<T>()`                                                        | `requestBody { schema = jsonSchema<T>() }`                               |
| Response Body       | Provides response body schema from `ContentNegotiation` writes | `call.respond<T>()`                                                        | `responses { HttpStatusCode.OK { schema = jsonSchema<T>() } }`           |
| Response Headers    | Includes custom headers for responses                          | `call.response.header("X-Foo", "Bar")`                                     | `responses { HttpStatusCode.OK { headers { header("X-Foo", "Bar") } } }` |
| Path Parameters     | Finds path parameter references                                | `call.parameters["id"]`                                                    | `parameters { path("id") }`                                              |
| Query Parameters    | Finds query parameter references                               | `call.queryParameters["name"]`                                             | `parameters { query("name") }`                                           |
| Request Headers     | Finds request header references                                | `call.request.headers["X-Foo"]`                                            | `parameters { header("X-Foo") }`                                         |
| Resource API routes | Infers call structure for the Resources routing API            | `call.get<List> { /**/ }; @Resource("/list") class List(val name: String)` | `parameters { query("name") }`                                           |

Inference follows extracted functions where possible and attempts to generate consistent documentation for typical
request and response flows.

#### Disable inference for an endpoint

If inference produces incorrect metadata for a specific endpoint, you can exclude it by adding an `ignore` marker:

```kotlin
// ignore!
get("/comments") {
    // ...
}
```

## Annotate routes {id="annotate-routes"}

To enrich the specification, Ktor supports two ways of annotating routes:

- [Comment-based annotations](#comment-annotations), analyzed by the compiler plugin.
- [Runtime route annotations](#runtime-route-annotations), defined using the `.describe {}` DSL.

You can use either approach or combine both.

### Comment-based route annotations {id="comment-annotations"}

Comment-based annotations provide metadata that cannot be inferred from code and integrate seamlessly with existing
routes.

Metadata is defined by placing a keyword at the start of a line, followed by a colon (`:`) and its value.

You can attach comments directly to route declarations:

```kotlin
```
{src="snippets/openapi-spec-gen/src/main/kotlin/com/example/Application.kt" include-lines="80-96"}

#### Formatting rules

- Keywords must appear at the start of the line.
- A colon (`:`) separates the keyword from its value.
- Plural forms (for example, `Tags`, `Responses`) allow grouped definitions.
- Singular forms (for example, `Tag`, `Response`) are also supported.
- Top-level bullet points (`-`) are optional and only affect formatting.

The following variants are equivalent:

```kotlin
/**
 * Tag: widgets
 * 
 * Tags:
 *   - widgets
 * 
 * - Tags:
 *  - widgets
 */
```

#### Supported comment fields

| Tag            | Format                                          | Description                      |
|----------------|-------------------------------------------------|----------------------------------|
| `Tag`          | `Tag: name`                                     | Groups the endpoint under a tag  |
| `Path`         | `Path: [Type] name description`                 | Path parameter                   |
| `Query`        | `Query: [Type] name description`                | Query parameter                  |
| `Header`       | `Header: [Type] name description`               | Header parameter                 |
| `Cookie`       | `Cookie: [Type] name description`               | Cookie parameter                 |
| `Body`         | `Body: contentType [Type] description`          | Request body                     |
| `Response`     | `Response: code contentType [Type] description` | Response definition              |
| `Deprecated`   | `Deprecated: reason`                            | Marks the endpoint as deprecated |
| `Description`  | `Description: text`                             | Extended description             |
| `Security`     | `Security: scheme`                              | Security requirements            |
| `ExternalDocs` | `ExternalDocs: href`                            | External documentation link      |


### Runtime route annotations {id="runtime-route-annotations"}

<primary-label ref="experimental"/>

In cases where compile-time analysis is insufficient, such as when using dynamic routing, interceptors, or conditional
logic, you can attach OpenAPI operation metadata directly to a route at runtime using the `.describe {}` extension
function.

Each annotated route defines an OpenAPI [Operation object](https://swagger.io/specification/#operation-object),
which represents a single HTTP operation (for example, `GET /users`) in the generated OpenAPI specification.
The metadata is attached to the routing tree at runtime and is consumed by the OpenAPI and Swagger UI plugins.

The `.describe {}` DSL maps directly to the OpenAPI specification. Property names and structure correspond to the
fields defined for an Operation object, including parameters, request bodies, responses, security requirements,
servers, callbacks, and specification extensions (`x-*`).

The runtime route annotations API is experimental and requires opting in using `@OptIn(ExperimentalKtorApi::class)`:

```kotlin
```
{src="snippets/openapi-spec-gen/src/main/kotlin/com/example/Application.kt" include-lines="103-132"}

> For a complete list of available fields, refer to the [OpenAPI specification](https://swagger.io/specification/#operation-object).
>
{style="tip"}

Runtime annotations are merged with compiler-generated and comment-based metadata.
When the same OpenAPI field is defined by multiple sources, values provided by runtime annotations take [precedence](#metadata-precedence).

## Hide routes from the OpenAPI specification

To exclude a route and its children from the generated OpenAPI document, use the `Route.hide()` function:

```kotlin
@OptIn(ExperimentalKtorApi::class)
get("/routes") {
    // ....
}.hide()
```

This is useful for internal, administrative, or diagnostic endpoints that should not be published, including routes used
to [generate the OpenAPI specification](#assemble-and-serve-the-specification) itself.

The OpenAPI and Swagger UI plugins call `.hide()` automatically, so their routes are excluded from the resulting
document.

## Schema inference

Ktor automatically generates JSON schemas for request and response types when building OpenAPI specifications. By
default, schemas are inferred from type references using `kotlinx-serialization` descriptors on data classes. This
allows most common data models to be documented without extra effort.

### Customize schemas with annotations

You can override automatically generated JSON schema fields by adding [`@JsonSchema`](https://api.ktor.io/ktor-openapi-schema/io.ktor.openapi/-json-schema/index.html)
annotations to your data classes. This allows you to add descriptions, mark fields as required, and more:

```kotlin
@JsonSchema.Description("Represents a news article")
data class Article(
    val title: String,
    val content: String
)
```

### Use reflection-based schema inference

For projects using Jackson or Gson instead of `kotlinx-serialization`, you can use reflection-based schema
inference. To do that, set the `schemaInference` field in the `Routing` source on the OpenAPI or SwaggerUI plugins:

```kotlin
openAPI("docs") {
    outputPath = "docs/routes"
    info = OpenApiInfo("Books API from routes", "1.0.0")
    source = OpenApiDocSource.Routing(
        contentType = ContentType.Application.Json,
        schemaInference = ReflectionJsonSchemaInference.Default,
    )
}
```

### Customize reflection behavior

You can provide a custom `SchemaReflectionAdapter` to handle annotations or naming conventions that are not directly
supported.

`SchemaReflectionAdapter` is a field of `ReflectionJsonSchemaInference`, which allows you to override default behavior,
such as property names, ignored fields, or nullability rules.

For example, you can customize the behavior to support Gson’s `@SerializedName` annotation:

```kotlin
ReflectionJsonSchemaInference(object : SchemaReflectionAdapter {
    override fun getName(type: KType): String? {
        return (type.classifier as? KClass<*>)?.let {
            findAnnotations(SerializedName::class)?.value ?: it.simpleName
        }
    }
})
```

## Generate and serve the specification

The OpenAPI specification is assembled at runtime from runtime route annotations and metadata generated by the compiler
plugin.

You can expose the specification in the following ways:

- [Assemble and serve the OpenAPI document manually](#assemble-and-serve-the-specification).
- Use the [OpenAPI](server-openapi.md) or [SwaggerUI](server-swagger-ui.md) plugins to serve the specification and 
interactive documentation.

### Assemble and serve the specification

To assemble a complete OpenAPI document at runtime, create an `OpenApiDoc` instance and provide the routes that should
be included in the specification.

The document is assembled from compiler-generated metadata and runtime route annotations from the routing tree. The
resulting `OpenApiDoc` instance always reflects the current state of the application.

You typically construct the document from a route handler and respond with it directly:

```kotlin
```
{src="snippets/openapi-spec-gen/src/main/kotlin/com/example/Application.kt" include-lines="44-47"}

In this example, the OpenAPI document is serialized using the [`ContentNegotiation`](server-serialization.md) plugin.
This assumes that a JSON serializer (for example, `kotlinx.serialization`) is installed.

No additional build or generation step is required. Changes to routes or annotations are reflected automatically the
next time the specification is requested.

> If you want to make serialization explicit or avoid relying on `ContentNegotiation`, you can encode the document
> manually and respond with a JSON:
> 
> ```kotlin
> call.respondText(
>   Json.encodeToString(docs),
>   ContentType.Application.Json
> )
>```
>
{style="note"}

### Serve interactive documentation

To expose the OpenAPI specification through an interactive UI, use the [OpenAPI](server-openapi.md)
and [Swagger UI](server-swagger-ui.md) plugins.

Both plugins assemble the specification at runtime and can read metadata directly from the routing tree.
They differ in how the documentation is rendered:
- The OpenAPI plugin renders documentation on the server and serves pre-generated HTML.
- The Swagger UI plugin serves the OpenAPI specification as JSON or YAML and renders the UI in the browser using
Swagger UI.

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

### Metadata precedence

The final OpenAPI specification is assembled at runtime by merging metadata contributed from multiple sources.

The following sources are applied, in order:

1. Compiler-generated metadata, including:
    - [Routing structure analysis](#routing-structure-analysis)
    - [Code inference](#code-inference)
2. [Comment-based route annotations](#comment-annotations)
3. [Runtime route annotations](#runtime-route-annotations)

When the same OpenAPI field is defined by multiple sources, values provided by runtime annotations
take precedence over comment-based annotations and compiler-generated metadata.

Metadata that is not explicitly overridden is preserved and merged into the final document.
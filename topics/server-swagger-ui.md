[//]: # (title: Swagger UI)

<primary-label ref="server-plugin"/>

<var name="artifact_name" value="ktor-server-swagger"/>
<var name="package_name" value="io.ktor.server.plugins.swagger"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server-swagger/io.ktor.server.plugins.swagger/swagger-u-i.html"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx-openapi"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>
The SwaggerUI plugin allows you to generate Swagger UI for your project.
</link-summary>

Ktor allows you to generate and serve Swagger UI for your project based on an OpenAPI specification.
With Swagger UI, you can visualize and interact with your API endpoints directly from the browser. 

You can provide the OpenAPI specification in one of the following ways:
- [Serve an existing YAML or JSON file](#static-openapi-file).
- [Generate the specification at runtime using the OpenAPI compiler extension and runtime APIs](#generate-runtime-openapi-metadata).

## Add dependencies {id="add_dependencies"}

Serving Swagger UI requires adding the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>


## Use a static OpenAPI file {id="static-openapi-file"}

To serve Swagger UI from an existing OpenAPI specification file, use the [`swaggerUI()`](%plugin_api_link%) function and
specify the file location.

The following example creates a `GET` endpoint at the `swagger` path and renders the Swagger UI from the provided
OpenAPI specification file:

```kotlin
import io.ktor.server.plugins.swagger.*

// ...
routing {
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
}
```

The plugin first looks for the specification in the application resources. If not found, it attempts to load it from
the file system using `java.io.File`.

## Generate runtime OpenAPI metadata

Instead of relying on a static file, you can generate the OpenAPI specification at runtime using metadata produced
by the OpenAPI compiler plugin and route annotations.

```kotlin
swaggerUI("/swaggerUI") {
    info = OpenApiInfo("My API", "1.0")
    source = OpenApiDocSource.RoutingSource(ContentType.Application.Json) {
        apiRoute.descendants()
    }
}
```

With this, you can access the generated OpenAPI documentation at the `/swaggerUI` path, reflecting the current state of
the application.

> For more information on the OpenAPI compiler extension and runtime APIs, see [](openapi-spec-generation.md).

## Configure Swagger UI

You can customize Swagger UI within the `swaggerUI() { }` block, for example, by specifying a custom Swagger UI version:

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="40,53-55,59"}

## Configure CORS {id="configure-cors"}

To ensure Swagger UI can access your API endpoints correctly, you must configure [Cross-Origin Resource Sharing (CORS)](server-cors.md).

The example below applies the following CORS configuration:
- `anyHost` enables cross-origin requests from any host.
- `allowHeader` allows the `Content-Type` client header used for [content negotiation](server-serialization.md).

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="36-39"}

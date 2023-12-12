[//]: # (title: Swagger UI)

<var name="artifact_name" value="ktor-server-swagger"/>
<var name="package_name" value="io.ktor.server.plugins.swagger"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-swagger/io.ktor.server.plugins.swagger/swagger-u-i.html"/>

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

Ktor allows you to generate and serve Swagger UI for your project based on the existing OpenAPI specification.
With Swagger UI, you can visualize and interact with the API resources. 

> The following tools are available for generating OpenAPI definitions from code and vice versa:
> - The [Ktor plugin](https://www.jetbrains.com/help/idea/ktor.html#openapi) for IntelliJ IDEA provides the ability to generate OpenAPI documentation for server-side Ktor applications.
> - The [OpenAPI generator](https://github.com/OpenAPITools/openapi-generator) allows you to create a Ktor project from your API definitions by using the [kotlin-server](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin-server.md) generator. Alternatively, you can use IntelliJ IDEA's [functionality](https://www.jetbrains.com/help/idea/openapi.html#codegen).
> 
{id="open-api-note"}


## Add dependencies {id="add_dependencies"}

Serving Swagger UI requires adding the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>


## Configure Swagger UI {id="configure-swagger"}

To serve Swagger UI, you need to call the [swaggerUI](%plugin_api_link%) method that creates a `GET` endpoint with Swagger UI 
at the `path` rendered from the OpenAPI specification placed at `swaggerFile`:

```kotlin
import io.ktor.server.plugins.swagger.*

// ...
routing {
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
}
```

This method tries to look up the OpenAPI specification in the application resources.
Otherwise, it tries to read the OpenAPI specification from the file system using `java.io.File`.

Optionally, you can customize Swagger UI inside the `swaggerUI` block.
For example, you can use another Swagger UI version or apply a custom style.

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="39,52-54,58"}

You can now [run](running.md) the application and open the `/swagger` page to see the available endpoints, and test them.


## Configure CORS {id="configure-cors"}

To make sure your API works nicely with Swagger UI, you need to set up a policy for [Cross-Origin Resource Sharing (CORS)](cors.md).
The example below applies the following CORS configuration:
- `anyHost` enables cross-origin requests from any host;
- `allowHeader` allows the `Content-Type` client header used in [content negotiation](serialization.md).

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="35-38"}

[//]: # (title: OpenAPI)

<var name="artifact_name" value="ktor-server-openapi"/>
<var name="package_name" value="io.ktor.server.plugins.openapi"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx-openapi"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor allows you to generate and serve OpenAPI documentation for your project based on the existing OpenAPI specification.

<include from="swagger-ui.md" element-id="open-api-note"/>


## Add dependencies {id="add_dependencies"}

Serving OpenAPI documentation requires adding the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>


## Configure OpenAPI {id="configure-swagger"}

To serve OpenAPI documentation, you need to call the `openAPI` method that creates a `GET` endpoint with documentation 
at the `path` rendered from the OpenAPI specification placed at `swaggerFile`:

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="38,52-53"}

This method tries to look up the OpenAPI specification in the application resources.
Otherwise, it tries to read the OpenAPI specification from the file system using `java.io.File`.

By default, the documentation is generated using `StaticHtml2Codegen`.
You can customize generation settings inside the `openAPI` block:

```kotlin
openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
     // this: OpenAPIConfig       
}
```

You can now [run](running.md) the application and open the `/openapi` page to see the generated documentation.
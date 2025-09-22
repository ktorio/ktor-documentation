[//]: # (title: OpenAPI)

<primary-label ref="server-plugin"/>

<var name="artifact_name" value="ktor-server-openapi"/>
<var name="package_name" value="io.ktor.server.plugins.openapi"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-openapi/io.ktor.server.plugins.openapi/open-a-p-i.html"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx-openapi"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>
The OpenAPI plugin allows you to generate OpenAPI documentation for your project.
</link-summary>

Ktor allows you to generate and serve OpenAPI documentation for your project based on an existing OpenAPI specification.
You can serve an existing YAML or JSON specification, or generate one using the
[OpenAPI extension](openapi-spec-generation.md) of the Ktor Gradle plugin.

## Add dependencies {id="add_dependencies"}

* Serving OpenAPI documentation requires adding the `%artifact_name%` artifact in the build script:

  <include from="lib.topic" element-id="add_ktor_artifact"/>

* Optionally, add the `swagger-codegen-generators` dependency if you want to customize a 
   [code generator](https://github.com/swagger-api/swagger-codegen-generators):

  <var name="group_id" value="io.swagger.codegen.v3"/>
  <var name="artifact_name" value="swagger-codegen-generators"/>
  <var name="version" value="swagger_codegen_version"/>
  <include from="lib.topic" element-id="add_artifact"/>

  You can replace `$swagger_codegen_version` with the required version of the `swagger-codegen-generators` artifact, for example, `%swagger_codegen_version%`.


## Configure OpenAPI {id="configure-swagger"}

To serve OpenAPI documentation, you need to call the [openAPI](%plugin_api_link%) method that creates a `GET` endpoint with documentation 
at the `path` rendered from the OpenAPI specification placed at `swaggerFile`:

```kotlin
import io.ktor.server.plugins.openapi.*

// ...
routing {
    openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
}
```

This method tries to look up the OpenAPI specification in the application resources.
Otherwise, it tries to read the OpenAPI specification from the file system using `java.io.File`.

By default, the documentation is generated using `StaticHtml2Codegen`.
You can customize generation settings inside the `openAPI` block:

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="40,56-58,59"}

You can now [run](server-run.md) the application and open the `/openapi` page to see the generated documentation.

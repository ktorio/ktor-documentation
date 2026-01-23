[//]: # (title: OpenAPI)

<primary-label ref="server-plugin"/>

<var name="artifact_name" value="ktor-server-openapi"/>
<var name="package_name" value="io.ktor.server.plugins.openapi"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server-openapi/io.ktor.server.plugins.openapi/open-a-p-i.html"/>

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

Ktor allows you to serve OpenAPI documentation based on an OpenAPI specification.

You can provide the OpenAPI specification in one of the following ways:

* [Serve an existing YAML or JSON file](#static-openapi-file).
* [Generate the specification at runtime using the OpenAPI compiler extension and runtime APIs](#generate-runtime-openapi-metadata).

In both cases, the OpenAPI plugin assembles the specification on the server and renders the documentation as HTML.

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

> In Ktor 3.4.0, the `OpenAPI` plugin requires the `ktor-server-routing-openapi` dependency.
> This was not an intentional breaking change and will be corrected in Ktor 3.4.1.
> Add the dependency manually if you are using Ktor 3.4.0 to avoid runtime errors.
>
{style="warning"}

## Use a static OpenAPI file {id="static-openapi-file"}

To serve OpenAPI documentation from an existing specification, use the [`openAPI()`](%plugin_api_link%) function with
a provided path to the OpenAPI document.

The following example creates a `GET` endpoint at the `openapi` path and renders the Swagger UI from the provided
OpenAPI specification file:

```kotlin
import io.ktor.server.plugins.openapi.*

// ...
routing {
    openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
}
```

The plugin first looks for the specification in the application resources. If not found, it attempts to load it from
the file system using `java.io.File`.

## Generate runtime OpenAPI metadata

Instead of relying on a static file, you can generate the OpenAPI specification at runtime using metadata produced
by the OpenAPI compiler plugin and route annotations.

In this mode, the OpenAPI plugin assembles the specification directly from the routing tree:

```kotlin
 openAPI(path = "openapi") {
    info = OpenApiInfo("My API", "1.0")
    source = OpenApiDocSource.Routing {
        routingRoot.descendants()
    }
}
```

With this, you can access the generated OpenAPI documentation at the `/openapi` path, reflecting the current state of the
application.

> For more information on the OpenAPI compiler extension and runtime APIs, see [](openapi-spec-generation.md).
> 
{style="tip"}

## Configure OpenAPI {id="configure-openapi"}

By default, documentation is rendered using `StaticHtml2Codegen`. You can customize the renderer inside the `openAPI {}`
block:

```kotlin
```
{src="snippets/json-kotlinx-openapi/src/main/kotlin/com/example/Application.kt" include-lines="40,56-58,59"}

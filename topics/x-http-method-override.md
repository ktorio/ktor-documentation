[//]: # (title: XHttpMethodOverride)

<var name="plugin_name" value="XHttpMethodOverride"/>
<var name="artifact_name" value="ktor-server-method-override"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx-method-override"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
%plugin_name% enables the capability to tunnel HTTP verbs inside the X-HTTP-Method-Override header.
</excerpt>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-method-override/io.ktor.server.plugins.methodoverride/-x-http-method-override.html) plugin enables the capability to tunnel HTTP verbs inside the `X-HTTP-Method-Override` header.
This might be useful if your server API handles multiple HTTP verbs (`GET`, `PUT`, `POST`, `DELETE`, and so on), but the client can only use a limited set of verbs (for example, `GET` and `POST`) due to specific limitations.
For instance, if the client sends a request with the `X-Http-Method-Override` header set to `DELETE`, Ktor will process this request using the `delete` [route handler](Routing_in_Ktor.md#define_route).


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

By default, `%plugin_name%` checks the `X-Http-Method-Override` header to determine the route that should handle the request.
You can customize a header name using the `headerName` property.

## Example {id="example"}

The HTTP request below uses the `POST` verb with the `X-Http-Method-Override` header set to `DELETE`:

```http request
```
{src="snippets/json-kotlinx-method-override/post.http"}

To handle such requests using the `delete` [route handler](Routing_in_Ktor.md#define_route), you need to install `%plugin_name%`:

```kotlin
```
{src="snippets/json-kotlinx-method-override/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [json-kotlinx-method-override](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/json-kotlinx-method-override).


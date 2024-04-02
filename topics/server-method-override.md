[//]: # (title: XHttpMethodOverride)

<var name="plugin_name" value="XHttpMethodOverride"/>
<var name="package_name" value="io.ktor.server.plugins.methodoverride"/>
<var name="artifact_name" value="ktor-server-method-override"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx-method-override"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
%plugin_name% enables the capability to tunnel HTTP verbs inside the X-HTTP-Method-Override header.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-method-override/io.ktor.server.plugins.methodoverride/-x-http-method-override.html) plugin enables the capability to tunnel HTTP verbs inside the `X-HTTP-Method-Override` header.
This might be useful if your server API handles multiple HTTP verbs (`GET`, `PUT`, `POST`, `DELETE`, and so on), but the client can only use a limited set of verbs (for example, `GET` and `POST`) due to specific limitations.
For instance, if the client sends a request with the `X-Http-Method-Override` header set to `DELETE`, Ktor will process this request using the `delete` [route handler](server-routing.md#define_route).


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

By default, `%plugin_name%` checks the `X-Http-Method-Override` header to determine the route that should handle the request.
You can customize a header name using the `headerName` property.

## Example {id="example"}

The HTTP request below uses the `POST` verb with the `X-Http-Method-Override` header set to `DELETE`:

```http request
```
{src="snippets/json-kotlinx-method-override/post.http"}

To handle such requests using the `delete` [route handler](server-routing.md#define_route), you need to install `%plugin_name%`:

```kotlin
```
{src="snippets/json-kotlinx-method-override/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [json-kotlinx-method-override](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx-method-override).


[//]: # (title: Conditional headers)

<var name="artifact_name" value="ktor-server-conditional-headers"/>
<var name="package_name" value="io.ktor.server.plugins.conditionalheaders"/>
<var name="plugin_name" value="ConditionalHeaders"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="conditional-headers"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

The [ConditionalHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-conditional-headers/io.ktor.server.plugins.conditionalheaders/-conditional-headers.html) plugin avoids sending the body of content if it has not changed since the last request. This is achieved by using the following headers:
* The `Last-Modified` response header contains a resource modification time. For example, if the client request contains the `If-Modified-Since` value, Ktor will send a full response only if a resource has been modified after the given date. Note that for [static files](Serving_Static_Content.md) Ktor appends the `Last-Modified` header automatically after [installing](#install_plugin) `ConditionalHeaders`.
* The `Etag` response header is an identifier for a specific resource version. For instance, if the client request contains the `If-None-Match` value, Ktor won't send a full response in case this value matches the `Etag`. You can specify the `Etag` value when [configuring](#configure) `ConditionalHeaders`.

## Add dependencies {id="add_dependencies"}

<include src="lib.topic" element-id="add_ktor_artifact_intro"/>
<include src="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.topic" element-id="install_plugin"/>


## Configure headers {id="configure"}

To configure `%plugin_name%`, you need to call the [version](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-conditional-headers/io.ktor.server.plugins.conditionalheaders/-conditional-headers-config/version.html) function inside the `install` block. This function provides access to a list of resource versions for a given `ApplicationCall` and `OutgoingContent`. You can specify the required versions by using the [EntityTagVersion](https://api.ktor.io/ktor-http/io.ktor.http.content/-entity-tag-version/index.html) and [LastModifiedVersion](https://api.ktor.io/ktor-http/io.ktor.http.content/-last-modified-version/index.html) class objects.

The code snippet below shows how to add a `Etag` and `Last-Modified` headers for CSS:
```kotlin
```
{src="snippets/conditional-headers/src/main/kotlin/com/example/Application.kt" lines="16-27"}

You can find the full example here: [conditional-headers](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/conditional-headers).

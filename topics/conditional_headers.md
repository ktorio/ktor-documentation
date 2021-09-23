[//]: # (title: Conditional headers)

<microformat>
<var name="example_name" value="conditional-headers"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The [ConditionalHeaders](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-conditional-headers/index.html) plugin avoids sending the body of content if it has not changed since the last request. This is achieved by using the following headers:
* The `Last-Modified` response header contains a resource modification time. For example, if the client request contains the `If-Modified-Since` value, Ktor will send a full response only if a resource has been modified after the given date. Note that for [static files](Serving_Static_Content.md) Ktor appends the `Last-Modified` header automatically after [installing](#install_plugin) `ConditionalHeaders`.
* The `Etag` response header is an identifier for a specific resource version. For instance, if the client request contains the `If-None-Match` value, Ktor won't send a full response in case this value matches the `Etag`. You can specify the `Etag` value when [configuring](#configure) `ConditionalHeaders`.


## Install ConditionalHeaders {id="install_plugin"}
<var name="plugin_name" value="ConditionalHeaders"/>
<include src="lib.xml" include-id="install_plugin"/>


## Configure headers {id="configure"}

To configure `ConditionalHeaders`, you need to call the [version](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-conditional-headers/-configuration/version.html) function inside the `install` block. This function provides access to a list of resource versions for a given [OutgoingContent](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-outgoing-content/index.html). You can specify the required versions by using the [EntityTagVersion](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-entity-tag-version/index.html) and [LastModifiedVersion](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-last-modified-version/index.html) class objects.

The code snippet below shows how to add a `Etag` and `Last-Modified` headers for CSS:
```kotlin
```
{src="snippets/conditional-headers/src/main/kotlin/com/example/Application.kt" lines="16-27"}

You can find the full example here: [conditional-headers](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/conditional-headers).

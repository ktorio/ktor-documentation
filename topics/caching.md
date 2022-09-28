[//]: # (title: Caching headers)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="CachingHeaders"/>
<var name="package_name" value="io.ktor.server.plugins.cachingheaders"/>
<var name="artifact_name" value="ktor-server-caching-headers"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="caching-headers"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [CachingHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-caching-headers/io.ktor.server.plugins.cachingheaders/-caching-headers.html) plugin adds the capability to configure the `Cache-Control` and `Expires` headers used for HTTP caching. You can [configure caching](#configure) in the following ways:
- Configure different caching strategies for specific content types, such as images, CSS and JavaScript files, and so on.
- Specify caching options on different levels: globally on the application level, on a route level, or for specific calls.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>

After installing `%plugin_name%`, you can [configure](#configure) caching settings for various content types.

## Configure caching {id="configure"}
To configure the `%plugin_name%` plugin, you need to define the [options](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-caching-headers/io.ktor.server.plugins.cachingheaders/-caching-headers-config/options.html) function to provide specified caching options for a given `ApplicationCall` and content type. The code snippet from the [caching-headers](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/caching-headers) example shows how to add the `Cache-Control` header with the `max-age` option for plain text and HTML:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" include-lines="14-24,52-53"}

The [CachingOptions](https://api.ktor.io/ktor-http/io.ktor.http.content/-caching-options/index.html) object accepts `Cache-Control` and `Expires` header values as parameters:

* The `cacheControl` parameter accepts a [CacheControl](https://api.ktor.io/ktor-http/io.ktor.http/-cache-control/index.html) value. You can use `CacheControl.MaxAge` to specify the `max-age` parameter and related settings, such as visibility, revalidation options, and so on. You can disable caching by using `CacheControl.NoCache`/`CacheControl.NoStore`.
* The `expires` parameter allows you to specify the `Expires` header as a `GMTDate` or `ZonedDateTime` value.

### Route level {id="configure-route"}

You can install plugins not only globally but also to [specific routes](Plugins.md#install-route). For instance, the example below shows how to add the specified caching header for the `/index` route:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" include-lines="25-32"}


### Call level {id="configure-call"}

If you need more fine-grained caching setup, you can configure caching options on a call level using the `ApplicationCall.caching` property. The example below shows how to configure caching options depending on whether a user is logged in or not:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" include-lines="40-51"}

> For logging in users, you can use the [Authentication](authentication.md) and [Sessions](sessions.md) plugins.
[//]: # (title: DoubleReceive)

<primary-label ref="server-plugin"/>

<var name="plugin_name" value="DoubleReceive"/>
<var name="package_name" value="io.ktor.server.plugins.doublereceive"/>
<var name="artifact_name" value="ktor-server-double-receive"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="double-receive"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-double-receive/io.ktor.server.plugins.doublereceive/-double-receive.html) plugin provides the ability to [receive a request body](server-requests.md#body_contents) several times with no `RequestAlreadyConsumedException` exception.
This might be useful if a [plugin](server-plugins.md) is already consumed a request body, so you cannot receive it inside a route handler.
For example, you can use `%plugin_name%` to log a request body using the [CallLogging](server-call-logging.md) plugin and then receive a body one more time inside the `post` [route handler](server-routing.md#define_route).

> The `%plugin_name%` plugin uses an experimental API that is expected to evolve in the upcoming updates with potentially breaking changes.
>
{type="note"}

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>

After installing `%plugin_name%`, you can [receive a request body](server-requests.md#body_contents) several times and every invocation returns the same instance.
For example, you can enable logging of a request body using the [CallLogging](server-call-logging.md) plugin...

```kotlin
```
{src="snippets/double-receive/src/main/kotlin/com/example/Application.kt" include-lines="16-23"}

... and then get a request body one more time inside a route handler.

```kotlin
```
{src="snippets/double-receive/src/main/kotlin/com/example/Application.kt" include-lines="25-28"}

You can find the full example here: [double-receive](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/double-receive).


## Configure %plugin_name% {id="configure"}
With the default configuration, `%plugin_name%` provides the ability to [receive a request body](server-requests.md#body_contents) as the following types:

- `ByteArray` 
- `String`
- `Parameters` 
- [data classes](server-serialization.md#create_data_class) used by the `ContentNegotiation` plugin

By default, `%plugin_name%` doesn't support:

- receiving different types from the same request;
- receiving a [stream or channel](server-requests.md#raw).

Set the `cacheRawRequest` property to `false`, if you do not need to receive different types from the same request or receive a stream or channel:

```kotlin
install(DoubleReceive) {
    cacheRawRequest = false
}
```

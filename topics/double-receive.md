[//]: # (title: DoubleReceive)

<var name="plugin_name" value="DoubleReceive"/>
<var name="artifact_name" value="ktor-server-double-receive"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="double-receive"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `%plugin_name%` plugin provides the ability to [receive a request body](requests.md#body_contents) several times with no `RequestAlreadyConsumedException` exception.
This might be useful if a [plugin](Plugins.md) is already consumed a request body, so you cannot receive it inside a route handler.
For example, you can use `%plugin_name%` to log a request body using the [CallLogging](call-logging.md) plugin and then receive a body one more time inside the `post` [route handler](Routing_in_Ktor.md#define_route).

> The `%plugin_name%` plugin use an experimental API that is expected to evolve in the upcoming updates with potentially breaking changes.
>
{type="note"}

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

After that, you can [receive a request body](requests.md#body_contents) several times and every invocation returns the same instance.
For example, call logging:

```kotlin
```
{src="snippets/double-receive/src/main/kotlin/com/example/Application.kt" lines="17-24"}

Route handler:

```kotlin
```
{src="snippets/double-receive/src/main/kotlin/com/example/Application.kt" lines="26-29"}

## Supported content types {id="content_types"}

Types that could be always received twice with this plugin are: 
- `ByteArray` 
- `String`
- `Parameters` 
- [types](serialization.md#create_data_class) provided by the `ContentNegotiation` plugin

Not every content could be received twice.
For example, a [stream or channel](requests.md#raw) can't be received twice unless `receiveEntireContent` option is enabled.

`receiveEntireContent` When enabled, for every request the whole content will be received and stored as a byte array. This is useful when completely different types need to be received. You also can receive streams and channels. Note that enabling this causes the whole receive pipeline to be executed for every further receive pipeline.

Receiving different types from the same call is not guaranteed to work without `receiveEntireContent` but may work in some specific cases. For example, receiving a text after receiving a byte array always works.

When `receiveEntireContent` is enabled, then receiving different types should always work. Also double receive of a channel or stream works as well. However,
`receive` executes the whole receive pipeline from the beginning so all content transformations and converters are executed every time that may be slower than with the option disabled.

Comparison:

| receiveEntireContent |      same type       | different type | channel |
|---------------------:|:--------------------:|:--------------:|:--------|
|              enabled |        re-run        |  yes, re-run   | yes     |
|             disabled | cached same instance |  generally no  | no      |


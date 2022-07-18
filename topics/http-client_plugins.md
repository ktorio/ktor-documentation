[//]: # (title: Plugins)

<link-summary>
Get acquainted with plugins that provide common functionality, for example, logging, serialization, authorization, etc.
</link-summary>

Many applications require common functionality that is out of scope of the application logic. This could be things like [logging](client_logging.md),  [serialization](serialization-client.md), or [authorization](auth.md). All of these are provided in Ktor by means of what we call **Plugins**. 


## Add plugin dependency {id="plugin-dependency"}
A plugin might require a separate [dependency](client-dependencies.md). For example, the [Logging](client_logging.md) plugin requires adding the `ktor-client-logging` artifact in the build script:

<var name="artifact_name" value="ktor-client-logging"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

You can learn which dependencies you need from a topic for a required plugin.


## Install a plugin {id="install"}
To install a plugin, you need to pass it to the `install` function inside a [client configuration block](create-client.md#configure-client). For example, installing the `Logging` plugin looks as follows:

```kotlin
```
{src="snippets/_misc_client/InstallLoggingPlugin.kt"}


## Configure a plugin {id="configure_plugin"}
You can configure a plugin inside the `install` block. For example, for the [Logging](client_logging.md) plugin, you can specify the logger, logging level, and condition for filtering log messages:
```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" lines="12-20"}


## Create a custom plugin {id="custom"}
If you want to create plugins, you can use the [standard plugins](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/plugins/) as a reference.

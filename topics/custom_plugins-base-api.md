[//]: # (title: Custom plugins - Base API)

<tldr>
<var name="example_name" value="custom-plugin-base-api"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

> Starting with v2.0.0, Ktor provides a new simplified API for [creating custom plugins](custom_plugins.md).
>
{type="note"}

Ktor exposes API for developing custom [plugins](Plugins.md) that implement common functionalities and can be reused in multiple applications. 
This API allows you to intercept different [pipeline](Pipelines.md) phases to add custom logic to a request/response processing.
For example, you can intercept the `Monitoring` phase to log incoming requests or collect metrics.

## Create a plugin {id="create"}
To create a custom plugin, follow the steps below:

1. Create a plugin class and [declare a companion object](#create-companion) that implements one of the following interfaces:
   - [BaseApplicationPlugin](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-base-application-plugin/index.html) if a plugin should work on an application level.
   - [BaseRouteScopedPlugin](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-base-route-scoped-plugin/index.html) if a plugin can be [installed to a specific route](Plugins.md#install-route).
2. [Implement](#implement) the `key` and `install` members of this companion object.
3. Provide a [plugin configuration](#plugin-configuration).
4. [Handle calls](#call-handling) by intercepting the required pipeline phases. 
5. [Install a plugin](#install).


### Create a companion object {id="create-companion"}

A custom plugin's class should have a companion object that implements the `BaseApplicationPlugin` or `BaseRouteScopedPlugin` interface.
The `BaseApplicationPlugin` interface accepts three type parameters:
- A type of pipeline this plugin is compatible with.
- A [configuration object type](#plugin-configuration) for this plugin.
- An instance type of the plugin object.

```kotlin
class CustomHeader() {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
        // ...
    }
}
```

### Implement the 'key' and 'install' members {id="implement"}

As a descendant of the `BaseApplicationPlugin` interface, a companion object should implement two members:
- The `key` property is used to identify a plugin. Ktor has a map of all attributes, and each plugin adds itself to this map using the specified key.
- The `install` function allows you to configure how your plugin works. Here you need to intercept a pipeline and return a plugin instance. We'll take a look at how to intercept a pipeline and handle calls in the [next chapter](#call-handling).

```kotlin
class CustomHeader() {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
        override val key = AttributeKey<CustomHeader>("CustomHeader")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomHeader {
            val plugin = CustomHeader()
            // Intercept a pipeline ...
            return plugin
        }
    }
}
```

### Handle calls {id="call-handling"}

In your custom plugin, you can handle requests and responses by intercepting [existing pipeline phases](Pipelines.md#ktor-pipelines) or [newly defined ones](Pipelines.md#phases). For example, the [Authentication](authentication.md) plugin adds the `Authenticate` and `Challenge` custom phases to the default pipeline. So, intercepting a specific pipeline allows you to access different stages of a call, for instance:

- `ApplicationCallPipeline.Monitoring`: intercepting this phase can be used for request logging or collecting metrics.
- `ApplicationCallPipeline.Plugins`: can be used to modify response parameters, for instance, append custom headers.
- `ApplicationReceivePipeline.Transform` and `ApplicationSendPipeline.Transform`: allow you to obtain and [transform data](#transform) received from the client and transform data before sending it back.

The example below demonstrates how to intercept the `ApplicationCallPipeline.Plugins` phase and append a custom header to each response:

```kotlin
class CustomHeader() {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
        override val key = AttributeKey<CustomHeader>("CustomHeader")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomHeader {
            val plugin = CustomHeader()
            pipeline.intercept(ApplicationCallPipeline.Plugins) {
                call.response.header("X-Custom-Header", "Hello, world!")
            }
            return plugin
        }
    }
}
```

Note that a custom header name and value in this plugin are hardcoded. You can make this plugin more flexible by [providing a configuration](#plugin-configuration) for passing the required custom header name/value.

> Custom plugins allow you to share any value related to a call, so you can access this value inside any handler processing this call. You can learn more from [](custom_plugins.md#call-state).


### Provide plugin configuration {id="plugin-configuration"}

The [previous chapter](#call-handling) shows how to create a plugin that appends a predefined custom header to each response. Let's make this plugin more useful and provide a configuration for passing the required custom header name/value. First, you need to define a configuration class inside a plugin's class:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" lines="11-14"}

Given that plugin configuration fields are mutable, saving them in local variables is recommended:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" lines="7-14,27"}

Finally, in the `install` function, you can get this configuration and use its properties 

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" lines="7-27"}



### Install a plugin {id="install"}

To [install](Plugins.md#install) a custom plugin to your application, call the `install` function and pass the desired [configuration](#plugin-configuration) parameters:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/Application.kt" lines="12-15"}


## Examples {id="examples"}

The code snippets below demonstrate several examples of custom plugins.
You can find the runnable project here: [custom-plugin-base-api](https://github.com/ktorio/ktor-documentation/blob/%current-branch%/codeSnippets/snippets/custom-plugin-base-api)

### Request logging {id="request-logging"}

The example below shows how to create a custom plugin for logging incoming requests:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/RequestLogging.kt"}

### Custom header {id="custom-header"}

This example demonstrates how to create a plugin that appends a custom header to each response:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt"}


### Body transformation {id="transform"}

The example below shows how to:
- transform data received from the client; 
- transform data to be sent to the client.

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/DataTransformation.kt"}


## Mapping of pipeline phases to new API handlers {id="mapping"}

Starting with v2.0.0, Ktor provides a new simplified API for [creating custom plugins](custom_plugins.md).
In general, this API doesn't require an understanding of internal Ktor concepts, such as [Pipelines](Pipelines.md), Phases, and so on. Instead, you have access to different stages of [handling requests and responses](#call-handling) using various handlers, such as `onCall`, `onCallReceive`, `onCallRespond`, and so on. 
The table below shows how pipeline phases map to handlers in a new API.

| Base API                               | New API                                                                              |
|----------------------------------------|--------------------------------------------------------------------------------------|
| `ApplicationCallPipeline.Setup`        | [on(CallSetup)](custom_plugins.md#other)                                             |
| `ApplicationCallPipeline.Plugins`      | [onCall](custom_plugins.md#on-call)                                                  |
| `ApplicationReceivePipeline.Transform` | [onCallReceive](custom_plugins.md#on-call-receive)                                   |
| `ApplicationSendPipeline.Transform`    | [onCallRespond](custom_plugins.md#on-call-respond)                                   |
| `Authentication.ChallengePhase`        | [on(AuthenticationChecked)](custom_plugins.md#other) (called after `ChallengePhase`) |


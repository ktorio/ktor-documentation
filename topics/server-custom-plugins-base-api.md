[//]: # (title: Custom plugins - Base API)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="custom-plugin-base-api"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Ktor provides a base API for developing custom [plugins](server-plugins.md) that implement reusable functionality across
multiple applications.

The base API lets you intercept different [pipeline](#pipelines) phases and add custom logic to request and response
processing. For example, you can intercept the `Monitoring` phase to log incoming requests or collect metrics.

## Create a plugin {id="create"}

To create a custom plugin with the base API:

1. Create a plugin class and [declare a companion object](#create-companion) that implements a plugin interface.
2. [Implement](#implement) the `key` property and the `install()` function in the companion object.
3. Provide a [plugin configuration](#plugin-configuration).
4. [Handle calls](#call-handling) by intercepting the required pipeline phases.
5. [Install the plugin](#install).


### Create a companion object {id="create-companion"}

A custom plugin's class must have a companion object that implements one of the following interfaces:

* [`BaseApplicationPlugin`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-base-application-plugin/index.html) for application-level plugins.
* [`BaseRouteScopedPlugin`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-base-route-scoped-plugin/index.html) for plugins that are [installed on a specific route](server-plugins.md#install-route).

The `BaseApplicationPlugin` interface accepts the following type parameters:

* The pipeline type that the plugin supports.
* The [configuration type](#plugin-configuration) for the plugin.
* The plugin instance type.

```kotlin
class CustomHeader() {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
        // ...
    }
}
```

### Implement the 'key' property and 'install()' function {id="implement"}

A companion object that implements `BaseApplicationPlugin` must define the following:

* The `key` property identifies the plugin. Ktor stores plugin instances in the application's attributes and uses this key
  to access the plugin instance.
* The `install()` function configures the plugin. In this function, intercept the required pipeline phases and return the
  plugin instance. The [Handle calls](#call-handling) section shows how to intercept a pipeline phase.

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

In a custom plugin, you can handle requests and responses by intercepting [existing pipeline phases](#pipelines) or newly defined
ones. For example, the [Authentication](server-auth.md) plugin adds the `Authenticate` and `Challenge` custom phases to the default
pipeline.

Intercepting a specific phase gives you access to a specific stage of call processing:

* `ApplicationCallPipeline.Monitoring`: use this phase for request logging, metrics, tracing, and similar monitoring
  tasks.
* `ApplicationCallPipeline.Plugins`: use this phase to handle calls or modify response parameters, such as appending
  custom headers.
* `ApplicationReceivePipeline.Transform` and `ApplicationSendPipeline.Transform`: use these phases to access and
  [transform](#transform) data received from the client or sent to the client.

The following example intercepts the `ApplicationCallPipeline.Plugins` phase and appends a custom header to each response:

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

In this example, the header name and value are hardcoded. To make the plugin reusable, [provide a configuration](#plugin-configuration)
that lets users specify the header name and value.

> Custom plugins can share values associated with a call between different handlers. To learn more, see
> [](server-custom-plugins.md#call-state).
>
{style="tip"}

### Provide plugin configuration {id="plugin-configuration"}

The [previous section](#call-handling) shows how to create a plugin that appends a predefined custom header to each
response. To make this plugin reusable, define a configuration that lets users specify the header name and value.

First, define a configuration class inside the plugin class:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" include-lines="11-14"}

You can update plugin configuration properties during plugin installation. If the plugin uses these values in interceptors,
store them in local variables inside the `install()` function:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" include-lines="7-14,27"}

Then, in the `install()` function, read the configuration and use its properties:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt" include-lines="7-27"}

### Install a plugin {id="install"}

To [install](server-plugins.md#install) a custom plugin to your application, call the `Application.install()` function and pass the required
[configuration](#plugin-configuration) parameters:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/Application.kt" include-lines="12-15"}


## Examples {id="examples"}

The following examples show several custom plugins built with the base API.

> For the full runnable project, see [custom-plugin-base-api](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin-base-api).
>
{style="tip"}

### Request logging {id="request-logging"}

The following example creates a custom plugin that logs incoming requests:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/RequestLogging.kt"}

### Custom header {id="custom-header"}

The following example creates a plugin that appends a custom header to each response:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/CustomHeader.kt"}


### Body transformation {id="transform"}

The following example creates a plugin that transforms request and response bodies:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/plugins/DataTransformation.kt"}

## Pipelines {id="pipelines"}

A [`Pipeline`](https://api.ktor.io/ktor-utils/io.ktor.util.pipeline/-pipeline/index.html) in Ktor is a collection of
interceptors grouped into one or more ordered phases. Each interceptor can run custom logic before and after request
processing continues.

[`ApplicationCallPipeline`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application-call-pipeline/index.html)
executes application calls. It defines the following phases:

* `Setup`: prepares a call and its attributes for processing.
* `Monitoring`: traces calls. Use this phase for request logging, metrics, error handling, and similar tasks.
* `Plugins`: handles calls. Most plugins intercept this phase.
* `Call`: completes a call.
* `Fallback`: handles calls that were not processed by earlier phases.

## Mapping of pipeline phases to new API handlers {id="mapping"}

You can use the simplified [custom plugins API](server-custom-plugins.md) to create custom
plugins. In most cases, this API does not require direct knowledge of internal Ktor concepts, such as pipelines and phases.
Instead, it provides handlers such as `onCall()`, `onCallReceive()`, and `onCallRespond()` for different stages of
[request and response handling](server-custom-plugins.md#call-handling).

The following table shows how base API pipeline phases map to simplified API handlers:

| Base API                               | New API                                                             |
|----------------------------------------|---------------------------------------------------------------------|
| before `ApplicationCallPipeline.Setup` | [`on(CallFailed)`](server-custom-plugins.md#other)                  |
| `ApplicationCallPipeline.Setup`        | [`on(CallSetup)`](server-custom-plugins.md#other)                   |
| `ApplicationCallPipeline.Plugins`      | [`onCall()`](server-custom-plugins.md#on-call)                      |
| `ApplicationCallPipeline.Call`         | [`onCallValidators()`](server-custom-plugins.md#on-call-validators) |
| `ApplicationReceivePipeline.Transform` | [`onCallReceive()`](server-custom-plugins.md#on-call-receive)       |
| `ApplicationSendPipeline.Transform`    | [`onCallRespond()`](server-custom-plugins.md#on-call-respond)       |
| `ApplicationSendPipeline.After`        | [`on(ResponseBodyReadyForSend)`](server-custom-plugins.md#other)    |
| `ApplicationSendPipeline.Engine`       | [`on(ResponseSent)`](server-custom-plugins.md#other)                |
| after `Authentication.ChallengePhase`  | [`on(AuthenticationChecked)`](server-custom-plugins.md#other)       |


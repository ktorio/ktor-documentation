[//]: # (title: Custom plugins)


<microformat>
<var name="example_name" value="custom-plugin"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor allows you to create custom [plugins](Plugins.md).

> The API described in this topic is in effect for v2.0.0 and higher. In the old versions, you can use the [legacy API](Creating_custom_plugins.md).


## Create and install your first plugin {id="first-plugin"}
1. Create a plugin by calling the `createApplicationPlugin` function:
   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt" lines="3-7"}
2. [Install](Plugins.md#install) the plugin:
   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" lines="11-12,35"}
3. Run you application to see the greeting in the console output:
   ```
   2021-10-14 14:54:08.269 [main] INFO  Application - Autoreload is disabled because the development mode is off.
   SimplePlugin is installed!
   2021-10-14 14:54:08.900 [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   {style="block"}
   
   In the next sections, we'll take a look how to handle calls on different stages and provide a plugin configuration.

## Call handling {id="call-handling"}
You can [handle requests](requests.md) and [send responses](responses.md) in your custom plugin.
In a custom plugin, you have access to different stages of handling calls using the following handlers:
* [onCall](#on-call) allows you to get request/response information, modify response parameters (append headers), log call information. To get and transform data, use `onCallReceive` / `onCallRespond`.
* [onCallReceive](#on-call-receive) allows you to get and transform received data.
* [onCallRespond](#on-call-respond) allows you to get and transform sent data.
* Each of the handlers above allows you to use [call.afterFinish](#call-after-finish).
* Share [call state](#call-state) between different handlers.

### onCall {id="on-call"}
Check request parameters, modify response parameters (append headers), log call information. 

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="6-10,14,18"}

One more example: 

```kotlin
val CustomHeaderPlugin = ServerPlugin.createApplicationPlugin(name = "CustomHeaderPlugin") {
    onCall { call ->
       call.response.headers.append("X-Custom-Header", "Hello, world!")
    }
}
```

Values are hardcoded, you can add a [plugin configuration](#plugin-configuration) to allows plugin users pass the required values.

### onCallReceive {id="on-call-receive"}
Transform received data

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" lines="6-10"}

Mention `onCallReceive.beforeTransform`

### onCallRespond {id="on-call-respond"}
Transform sent data

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" lines="12-20"}

Mention `onCallRespond.beforeTransform` and `onCallRespond.afterTransform`

### call.afterFinish {id="call-after-finish"}
Release resources, handle exceptions:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="11-13"}

### Share call state {id="call-state"}

* CallID
* time between `onCall` and `onCallReceive` - time until start reading the body

You can also acceess attributes in a route handler.


## Handle application shutdown {id="handle-shutdown"}

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="15-17"}

## Before/after plugin
### Before/after specific plugins
### Before all plugins


## Store plugin state {id="plugin-state"}

## Provide plugin configuration {id="plugin-configuration"}

## Access application settings
Config and environment

## Working with databases
### Suspendable
### Blocking

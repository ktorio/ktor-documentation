[//]: # (title: Custom plugins)


<microformat>
<var name="example_name" value="custom-plugin"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Starting with v2.0.0, Ktor provides a new API for creating custom [plugins](Plugins.md). In general, this API doesn't require an understanding of internal Ktor concepts, such as [Pipelines](Pipelines.md), Phases, and so on. Instead, you have access to different stages of [handling requests and responses](#call-handling) using the `onCall`, `onCallReceive`, and `onCallRespond` handlers.

> The API described in this topic is in effect for v2.0.0 and higher. For the old versions, you can use the [legacy API](Creating_custom_plugins.md).


## Create and install your first plugin {id="first-plugin"}

In this section, we'll demonstrate how to create and install your first plugin. You can use [ktor-get-started-sample](intellij-idea.xml) as a starting project.

1. To create a plugin, call the `createApplicationPlugin` function and pass a plugin name:
   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt" lines="3-7"}
   
   This function returns the `ApplicationPlugin` instance that will be used in the next step to install a plugin.
2. To [install a plugin](Plugins.md#install), pass the created `ApplicationPlugin` instance to the `install` function in the application's initialization code:
   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" lines="11-12,32"}
3. Finally, [run](running.md) you application to see the plugin's greeting in the console output:
   ```Bash
   2021-10-14 14:54:08.269 [main] INFO  Application - Autoreload is disabled because the development mode is off.
   SimplePlugin is installed!
   2021-10-14 14:54:08.900 [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   
   
You can find the full example here: [SimplePlugin.kt](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt). In the following sections, we'll look at how to handle calls on different stages and provide a plugin configuration.

## Handle calls {id="call-handling"}

In your custom plugin, you can [handle requests](requests.md) and [responses](responses.md) by using a set of handlers that provide access to different stages of a call:

* [onCall](#on-call) allows you to get request/response information, modify response parameters (for instance, append custom headers), and so on.
* [onCallReceive](#on-call-receive) allows you to obtain and transform data received from a client.
* [onCallRespond](#on-call-respond) allows you to transform data before sending it to a client.
* Each of the handlers above provides the ability to use [call.afterFinish](#call-after-finish).
* If required, you can share a [call state](#call-state) between different handlers using `call.attributes`.

### onCall {id="on-call"}

The `onCall` handler accepts the `ApplicationCall` as a lambda argument. This allows you to access to request/response information and modify response parameters (for instance, [append custom headers](#custom-header)). If you need to transform a request/response body, use [onCallReceive](#on-call-receive)/[onCallRespond](#on-call-respond).

#### Example 1: Request logging {id="request-logging"}

The example below shows how to use `onCall` to create a custom plugin for logging incoming requests:
```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="6-10,16,20"}

If you install this plugin, the application will show requested URLs in a console, for example:
```Bash
Request URL: http://0.0.0.0:8080/
Request URL: http://0.0.0.0:8080/index
```


#### Example 2: Custom header {id="custom-header"}
This example demonstrates how to create a plugin that appends a custom header to each response:
```kotlin
val CustomHeaderPlugin = ServerPlugin.createApplicationPlugin(name = "CustomHeaderPlugin") {
    onCall { call ->
        call.response.headers.append("X-Custom-Header", "Hello, world!")
    }
}
```

As a result, a custom header will be added to all responses:
```HTTP
HTTP/1.1 200 OK
X-Custom-Header: Hello, world!
```
   
Note that a custom header name and value in this plugin are hardcoded. You can make this plugin more flexible by providing a [configuration](#plugin-configuration) for passing the required custom header name/value.

### onCallReceive {id="on-call-receive"}

The `onCallReceive` handler provides the `transformBody` function and allows you to transform data received from a client. Suppose a client makes a sample `POST` request that contains `10` as `text/plain` in its body:

```HTTP
```
{src="snippets/custom-plugin/post.http"}

To [receive this body](requests.md#objects) as an integer value, you need create a route handler for `POST` requests and call `call.receive` with the `Int` parameter:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" lines="27-28,30"}

Now let's create a plugin that receives a body as an integer value and adds `1` to it. To do this, we need to handle `transformBody` inside `onCallReceive` as follows:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" lines="6-16,27"}

`transformBody` in the code snippet above works as follows:

1. `TransformContext` is a [lambda receiver](https://kotlinlang.org/docs/scope-functions.html#context-object-this-or-it) that contains type information about the current request. In the example above, the `TransformContext.requestedType` property is used to check the requested data type.
2. `data` is a lambda argument that allows you to receive a request body as [ByteReadChannel](https://api.ktor.io/ktor-io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) and convert it to the required type. In the example above, `ByteReadChannel.readUTF8Line` is used to read a request body. 
3. Finally, you need to transform and return data. In our example, `1` is added to the received integer value.

You can find the full example here: [DataTransformationPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt).


### onCallRespond {id="on-call-respond"}

`onCallRespond` also provides the `transformBody` handler and allows you to transform data to be sent to a client. This handler is executed when the `call.respond` function is invoked in a route handler. Let's continue with the example from [onCallReceive](#on-call-receive) where an integer value is received in a `POST` request handler:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" lines="27-30"}

Calling `call.respond` invokes the `onCallRespond`, which is in turn allows you to transform data to be sent to a client. For example, the code snippet below shows how to add `1` to the initial value:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" lines="18-26"}

You can find the full example here: [DataTransformationPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt).

> There is also the `onCallRespond.afterTransform` handler that is invoked after data transformation is performed.

### call.afterFinish {id="call-after-finish"}

The next handler is `call.afterFinish` that allows you to release resources related to a current call and handle exceptions:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="11-15"}

You can find the full example here: [RequestLoggingPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt).

### Share call state {id="call-state"}

* CallID
* time between `onCall` and `onCallReceive` - time until start reading the body

Benchmark example:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationBenchmarkPlugin.kt" lines="6-18"}


Result:

```Bash
Request URL: http://localhost:8080/transform-data
Read body delay (ms): 52
```

You can also access attributes in a route handler.


## Handle calls before/after other plugins

You can tell that you need to execute some specific handlers of your plugin strictly before/after same handlers of some other plugin have already been executed. There are methods:

```kotlin
// Some key for the first plugin:
val someKey = AttributeKey<String>("SomeKey")
val pluginFirst = ServerPlugin.createApplicationPlugin("First") {
   onCall { call ->
      call.attributes.put(someKey, "value") // passing data to pluginSecond
      println("first plugin onCall (saved value)")
   }
}
val pluginSecond = ServerPlugin.createApplicationPlugin("Second") {
   afterPlugins(pluginFirst) { // everything inside this block will be executed as intended but strictly before same handlers of pluginFirst were already executed:
      onCall { call ->
         val data = call.attributes[someKey]
         println("second plugin onCall, data = $data")
      }
   }
}
```


## Provide plugin configuration {id="plugin-configuration"}
You can do it if you define a configuration class. It can be any class with no actual restrictions, let's say you need a couple of params and a method:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt" lines="16-19"}

Now, in order to make this config working with your plugin, you should provide a lambda that creates a new instance of `PluginConfiguration`:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt" lines="5-14"}

Install and configure the plugin:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" lines="15-18"}

## Handle application shutdown {id="handle-shutdown"}

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="17-19"}


## Access application settings
### Config

You can access a configuration of your server (`ApplicationConfig`) via a field `configuration` of `ApplicationPlugin`. You can also access  `ApplicationConfig.port` and `ApplicationConfig.host` from there:
```kotlin
val MyPlugin = ServerPlugin.createApplicationPlugin(name = "MyPlugin") {
   val host = configuration.host
   val port = configuration.port
   println("Listening on $host:$port")
}
```

### Environment
You can also access `ApplicationEnvironment` via `environment` field the same way as you did with `configuration`. For example, it can be useful to get a `log` subfield (the global instance of the logger associated with the current application).
```kotlin
val MyPlugin = createPlugin("Plugin") {
   val isDevMode = environment.developementMode
   onCall { call ->
        if (isDevMode) {
            println("handling request ${call.request.uri}")
        }
    }
}
```




## Miscellaneous

### Store plugin state {id="plugin-state"}
This can be done by just capturing any value from handler lambda. But <b>please note that it is recommended to make all state values thread safe</b> by using concurrent data structures and atomic data types
```kotlin
val Plugin = createPlugin("Plugin") {
    val activeRequests: AtomicInt = atomic { 0 }
    
    onCall {
        activeRequests.incrementAndGet()
    }
    onCallRespond {
        activeRequests.decrementAndGet()
    }
}
```

### Databases
Can I use Ktor Plugin with suspendable databases?
Yes! All the handlers are `suspend` functions, so there is no problem with calling any suspendable database operations inside your plugin.
Just call database from any place inside your plugin. But don't forget to deallocate resources (see [](#call-after-finish) and [](#handle-shutdown) sections).

Can I use Ktor Plugin with blocking databases?
As Ktor uses suspend functions and coroutines everywhere, calling a blocking database directly can be dangerous because a coroutine that performs a blocking call can be blocked and then suspended forever.
In order to prevent this you need to create a separate [CoroutineContext](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html):
```kotlin
val databaseContext = newSingleThreadContext("DatabaseThread")
```
Then, once your context is created you should wrap each call to your database into `withContext` call:

```kotlin
onCall {
    withContext(databaseContext) {
        database.access(...) // some call to your database
    }
}
```

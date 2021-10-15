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

## Handle calls {id="call-handling"}
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
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="6-10,16,20"}

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
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="11-15"}

### Share call state {id="call-state"}

* CallID
* time between `onCall` and `onCallReceive` - time until start reading the body

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


## Handle application shutdown {id="handle-shutdown"}

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" lines="17-19"}


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

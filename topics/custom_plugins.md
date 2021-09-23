[//]: # (title: Custom plugins)

## Why?
Because we aim to provide a simpler version of the [legacy API](Creating_custom_plugins.md) that would fit into most common cases without limiting the core functionality, but will be easy to use for new users and will not require deep understanding of internal Ktor concepts (such as Pipelines, Phases, etc.)

What requirements did we follow?
- Simplicity
- If you are familiar with how to write a server in Ktor, it should be easy to write a Ktor Plugin as well.
- Backward compatibility with old API, i.e. same usage as it was before. So, no worries, all your `install(PluginName)` code will still make sense!

## What should I start with?

Ok, let's assume that you want to create your first Ktor Plugin but you still don't have any idea of what it should do. So, you want to create the simplest plugin possible. Well, let's reformulate this idea in terms of new API:
```kotlin
// Note: the following code can be executed anywhere 
// but it's recommended to make UselessPlugin variable public and available to plugin users
val UselessPlugin = createApplicationPlugin(name = "UselessPlugin") { 
    // Installation script that will be executed every time you install your plugin:
    println("plugin installed")
 }
```
Congratulations! You've just created your first Ktor plugin using the new API. As you may have guessed by its name, this plugin is useless, i.e. it does nothing.
But you can still install this plugin into your server:
```kotlin
fun Application.module() {
  install(UselessPlugin) // "plugin installed" will be printed
}
```
And you can also get the "instance" of a plugin installed to the current application and check if your plugin is installed:
```kotlin
fun Application.main() {
    val pluginInstance = plugin(UselessPlugin)
    if (pluginInstance != null) {
        println("Plugin was installed!")
    }
}
```

## How can I make a plugin useful for my app?
You can provide a callback to a `createApplicationPlugin` function that is needed to define how your newly created plugin should change the behaviour of the app.
There are 3 main functions available in order to do that:
### How to execute my code when HTTP request is received?
You can do it with`onCall` handler that provides a hook that will be executed when a HTTP request comes in. Here is how it can be defined:
```kotlin
val UsefulPlugin = createApplicationPlugin("UsefulPlugin") {
    onCall { call ->  // request: ApplicationCall
        println("onCall for url = \"${request.uri}\"")
    }
}
```
Now let's take a look at the server with this plugin installed:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
  install(UsefulPlugin)
  routing {
    println("server start")
    // HTTP request for url = "/route1"
    get("/route1") {
      println("get \"route1\"")
    }
  }
}
```
If you run this server and open your browser with a page `localhost:8080/route1` the output of the code above would be:
```text
server start
onCall for url = "route1"
get "route1"
```
### How to execute my code when my server receives data from a client?
You can use `onCallReceive` handler that will be executed when user calls `call.receive()`, i.e. when server receives some data when handling an HTTP request. Usage:
```kotlin
val UsefulPlugin = createApplicationPlugin("UsefulPlugin") {
    onCallReceive { call -> // call: ApplicationCall
        println("onCallReceive handler")
    }
}
```
Now let's take a look at the server with this plugin installed:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
  install(UsefulPlugin)
  routing {
    println("server start")
    get("/route2") {
      println("get \"/route2\"")
      val data = call.receive<String>()
      println("data received")
    }
  }
}
```
Output of the code above would be:
```text
server start
get "route2"
onCallReceive handler
data received
```
### How to execute my code when my server sends a response to client?
Use `onCallRespond` handler to execute some code when user calls `call.respond(...)`, i.e. when server responds with some result to a client. Usage:
```kotlin
val UsefulPlugin = createApplicationPlugin("UsefulPlugin") {
    onCallRespond { call -> // call: ApplicationCall
        println("onCallRespond handler")
    }
}
```
Now let's take a look at the server with this plugin installed:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
  install(UsefulPlugin)
  routing {
    println("server start")
    get("/route3") {
      println("get \"/route3\"")
      val data = call.receive<String>()
      println("data received")
      call.respondText(data)
      println("data was sent")
    }
  }
}
```
Output of the code above would be:
```text
server start
get "route3"
data received
onCallRespond handler
data was sent
```

## How to provide configuration for my plugin?
You can do it if you define a configuration class. It can be any class with no actual restrictions, let's say you need a couple of params and a method:
```kotlin
public class MyConfig {
    // params that can be changed/tuned when installing a plugin:
    var useCookies: Boolean = false
    var myName: String = "Kname"
    // accessible but not changeable params:
    val iAmAlwaysTrue = true
   
    // methods that can change config (useful inside `install`)
    fun addPassword(p: String) {
        passwords.add(p)
    }
    // methods that are just useful:
    fun hasPassword(): Boolean = passwords.isNotEmpty()
    // private values:
    private val passwords: MutableList<String> = mutableListOf<String>()
}
```

Now, in order to make this config working with your plugin, you should provide a lambda that creates a new instance of `MyConfig`:
```kotlin
val ConfigurablePlugin = createApplicationPlugin(
    "ConfigurablePlugin", 
    createConfiguration = { 
         // This will be executed before installation script: 
         MyConfig()
    }) {
   ...
}
```

Now you can install your plugin and configure it if you want:

```kotlin
install(ConfigurablePlugin) {
    useCookies = true
    myName = "NewName"
    if (iAmAlwaysTrue) {
        while (!hasPassword()) {
            addPassword("12345")
        }
    }
}
```

This configuration can be accessed and used from your plugin by calling `pluginConfig`. Let's get back to a `ConfigurablePlugin` example:
```kotlin
val ConfigurablePlugin = createApplicationPlugin("ConfigurablePlugin", createConfiguration = { MyConfig() }) {
   onCall {
      if (pluginConfig.useCookies) { ... }
   }
   println("Name: " + pluginConfig.myName)
}
```

## How I can keep a state for my plugin?
This can be done by just capturing any value from handler lambda. But <b>please note that it is recommended to make all state values thread safe</b> by using concurrent data structures and atomic data types
```kotlin
val Plugin = createApplicationPlugin("Plugin") {
    val activeRequests: AtomicInt = atomic { 0 }
    
    onCall {
        activeRequests.incrementAndGet()
    }
    onCallRespond {
        activeRequests.decrementAndGet()
    }
}
```
## How I can store a state associated with a single call?
This can be done via `call.attributes` that can be seen as a Key-Value map for different data types defined by keys. Let's take a look at how it works:
```kotlin
val Plugin = createApplicationPlugin("Plugin") {
    // First, you need to define a special key AttributeKey<...> for your desired data type that you need to store. For example, let's assume that you need to store a String value:
    val MySpecialKey = AttributeKey<String>("MySpecialKey") // please, make sure you give unique names for your keys
    // Then, you can use call.attributes.put(MySpecialKey, <some value>) in any handler in order to associate data with the current call:
    onCall { call ->
        request.call.attributes.put(MySpecialKey, "my value")
    }
    // And after you've done that, you can then access this data from call's attributes from some other handler via call.attributes.get(MySpecialKey):
    onCallRespond { call ->
        val callValue = call.attributes.get(MySpecialKey) // will be equal to "my value"
    }
```

## How can I transform the data (message) that is being responded to a client?
Let's try to implement a very simple Serialization feature that would allow to define how to serialize particular data types into ByteArray.
This can be done using `transformRespondBody { ... }` method inside `onCallRespond` and `transformReceiveBody { ... }`  method inside `onCallReceive` :
```kotlin
public val Incrementor = createApplicationPlugin("Incrementor") {
    onCallRespond {
        transformRespondBody { data ->
            // When responding numbers, increment:
            val result = when (data) {
                is Int -> data + 1
                else -> data
            }
            println("sending $result to client")
            return@transformRespondBody result
        }
    }
    onCallReceive { call ->
        transformReceiveBody { data ->
            println("received $data from client")
            // When receiving numbers, decrement:
            when (data) {
                is Int -> data + 1
                else -> data
            }
        }
    }
}
``` 
Now if you install such `Incrementor` plugin:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(Incrementor)
    routing {
        get("/number") {
            val request = call.receive<Int>()
            println("value = $request")
            call.respond(request)
        }
    }
}
```
Now, if you make a `/number` request to the server and send a body with value `100` you will see the following log on your server console:
```text
received 100 from client
value = 99
sending 100 to client
```
As you can see, the received data was transformed from 100 to 99, and then response was transform back from 99 to 100.
This is useful to write any sort of serializers, data convertors, encryptors, etc. You can see the source code of [ContentNegotiation](https://ktor.io/docs/serialization.html) if you are interested in a real world example.

## What if I allocate some resources associated with a call, how they can be deallocated?
They can be deallocated using a `onCallFinished { ... }` hook that is available inside any handler (`onCall { ... }`, `onCallReceive { ... }`, etc.).
Let's say you need to read from some `InputStream` and then close it when the work is finished:

```kotlin
internal val ResourceKey = AttributeKey<BufferedReader>()
public val FileReaderPlugin = createApplicationPlugin("FileReaderPlugin") {
    onCall { call ->
        val resource = File("resource.html").inputStream().bufferedReader()
        call.attributes.put(ResourceKey, resource)
        // Please note that onCallFinished does not belong to onCall
        // Will be executed when this call stops being processed. 
        onCallFinished {
            resource.close()
            println("resource closed!")
        }
        // Then, you can safely work with this BufferedReader:
        println(resource.readLine())
    }
    // Note: you can use this resource in any handler because onCallFinished will be executed after all possible handlers 
    // when the call processing was finished, and hence your resource will be closed anyway. 
    onCallRespond {
        val resource = call.attributes.get(ResourceKey)!!
        println(resource.readLine())
    }
}
```

Let's assume that the content of "resource.html" is the following:

```text
This is a first line
Here is the second line
Some other
text
here
```
And now you can install the feature:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(FileReaderPlugin)
    routing {
        get("/file") {
            call.respond("Hello, world!")
        }
    }
}
```
Once you open `0.0.0.0:8080/file` in your browser, you'll see the following output in your server console:
```text
This is a first line
Here is the second line
resource closed!
```
## Ok, but what if I need to handle the case of the whole server shutdown to deallocate resources?
Well, then you can use `applicationShutdownHook { ... }` handler that is accessible everywhere inside your plugin definition:

```kotlin
val MyPlugin = createApplicationPlugin("Plugin") {
    val logFile = File("log.txt").outputStream().bufferedWriter()
    applicationShutdownHook {
        // Log file will be closed once your server shuts down:
        logFile.close()
    }
    onCall { call ->
        logFile.write("request to ${request.uri}")
    }
}
```

## Handling exceptions
An exception can happen any time when processing, sending, or receiving from an HTTP request. To handle exceptions you can just use `onCall.handleException { cause: Throwable -> ... }` handler:

```kotlin
createApplicationPlugin("Plugin") {
     ...
     onCall.handleException { call, cause ->
         println("Exception $cause happened during a call to \"${call.request.uri}\"")
     }
}
```

## Accessing application configuration.
You can access a configuration of your server (`ApplicationConfig`) via a field `configuration` of `ApplicationPlugin`. You can also access  `ApplicationConfig.port` and `ApplicationConfig.host` from there:

```kotlin
val MyPlugin = createApplicationPlugin("Plugin") {
   val host = configuration.host
   val port = configuration.port
    onCall { call ->
        println("handling request ${host}:${port}/${request.uri}")
    }
}
```

Usage:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(MyPlugin)
    routing {
        get("/some/path") {
            call.respond("Hello, world!")
        }
    }
}
```

After requesting `/some/path` in client you'll see:
```text
handling request 0.0.0.0:8080/some/path
```
## Accessing application environment.
You can also access `ApplicationEnvironment` via `environment` field the same way you did with `configuration`. For example, it can be useful to get a `log` subfield (the global instance of the logger associated with the current application).

```kotlin
val MyPlugin = createApplicationPlugin("Plugin") {
   val isDevMode = environment.developementMode
    onCall { call ->
        if (isDevMode) {
            println("handling request ${request.uri}") // This will be printed only if development mode is on
        }
    }
}
```
## Advanced use cases
### What if I need to execute some code before all other plugins?
Short answer: Use `onCall.setup { ... }`.
Imagine you need to implement a simple Benchmark feature that would compute the time that was spent on processing some particular call with all features. This would require you to calculate the difference between the start time when a call has just been received and the end time after the call handling pipeline has finished it's work.
You can use the following code snippet to do that:

```kotlin
// Key to access time from attributes:
private val BenchmarkKey = AttributeKey<Long>("BenchmarkKey")
private val Benchmark = createApplicationPlugin("Benchmark") {
    val log = Logger.getLogger("Benchmark")
    onCall.setup { call ->
        val startTime = System.currentTimeMillis()
        onCallFinished{
            val endTime = System.currentTimeMillis()
            val requestTime = endTime - startTime
        
            println("Request ${request.uri} took: $requestTime (ms)")
        }
    }
}
```

If you install this feature in your app, it would print request time for each request:
```text
Request /path took: 112 (ms)
Request /other/path took: 99 (ms)
```
### What if I need to execute some code after response transformation has been made?
Short answer: You can use `onCallRespond.afterTransform { ... }`.
Imagine you need to implement simple plugin that will respond with "Sorry, 404 happend" in case of status 404.
In order to do that, you will need to perform a status check after the last response data transformation has been made, i.e. the current data type is either `OutgoingContent` or `HttpStatusCode`. The code for such plugin can be the following:

```kotlin
private val Status404Reporter = createApplicationPlugin("Status404Reporter") {
    onCallRespond.afterTransform { call, body -> // call: ApplicationCall, body: Any
        val status = when (body) {
            is OutgoingContent -> body.status
            is HttpStatusCode -> body
            else -> null
        } ?: return@afterTransform
        if (status == HttpStatusCode.NotFound) {
            transformRespondBody { "Sorry, 404 happened" }
        }
    }
}
```
Note: there is already a generic [StatusPages](https://ktor.io/docs/status-pages.html) plugin in Ktor.
Usage:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(Status404Reporter)
}
```
After requesting `0.0.0.0:8080/give/me/404` in your browser you'll see the request "Sorry, 404 happened".
### Ok, but is there a way to execute my custom code before response data transformations? (TODO)
Use `onCallRespond.beforeTransform { ... }` for that.
Open design question: it is now used only in `Sessions` feature and nowhere else. Should we still provide this handler for users?
### And how can I add my code before any transformations of received data have been made? (TODO)
Use `onCallReceive.beforeTransform { ... }` for that.
Open design question: it is now used only in `DoubleReceive` feature and nowhere else. Should we still provide this handler for users?
It is useful for caching of received data.
## How to execute before/after other plugin?
You can tell that you need to execute some specific handlers of your plugin strictly before/after same handlers of some other plugin have already been executed. There are methods:
- `beforePlugin(otherPlugin) { ... }`
- `afterPlugin(otherPlugin) { ... }`
  Example:
```kotlin
// Some key for the first plugin:
val FirstKey = AttributeKey<String>()
val pluginFirst = createApplicationPlugin("First") {
    onCall { call ->
        call.attributes.put(FirstKey, "value") // passing data to pluginSecond
        println("first plugin onCall (saved value)")
    }
}
val pluginSecond = createApplicationPlugin("Second", {}) {
    afterPlugin(pluginFirst) { // everything inside this block will be executed as intended but strictly before same handlers of pluginFirst were already executed:
        onCall { call ->
            val data = call.attributes.get(FirstKey)
            println("second plugin onCall, data = $data")
        }
    }
}
```
You can install these 2 plugins in any order:
```kotlin
embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    // Order of the installation does not matter:
    install(SecondPlugin)
    install(FirstPlugin)
    routing {
        get {
            call.respondText("Hello!")
        }
    }
}
```
When you run this server and open your browser at `0.0.0.0:8080/` you'll see the following text in your server console:
```text
first plugin onCall (saved value)
second plugin onCall, data = value
```
As you can see, we've just guaranteed the order between 2 plugins and that allowed us to pass some data between them.
## Can I use Ktor Plugin with suspendable databases? (TODO)
Yes! All the handlers are `suspend` functions, so there is no problem with calling any suspendable database operations inside your plugin.
Just call database from any place inside your plugin. But please don't forget to deallocate resources (see `What if I allocate some resources associated with a call, how they can be deallocated` and `Ok, but what if I need to handle the case of the whole server shutdown to deallocate resources` sections)
## Can I use Ktor Plugin with blocking databases? (TODO)
As Ktor uses suspend functions and coroutines everywhere, calling a blocking database directly can be dangerous because a coroutine that performs a blocking call can be blocked and then suspended forever.
In order to prevent this you need to create a separate [CoroutineContext](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html):

```kotlin
val databaseContext = newSingleThreadContext("DatabaseThread")
```

Then, once your context is created you should wrap each call to your database into `withContext` call:

```kotlin
onCall {
    ...
    withContext(databaseContext) {
        database.access(...) // some call to your database
    }
    ...
}
```
## `ApplicationCallPipeline` in config example (TODO)
#### `onCallRespond.afterTransform { ... }` and `Sessions` -- TODO (is it needed)

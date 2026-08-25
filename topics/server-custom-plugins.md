[//]: # (title: Custom server plugins)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="custom-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to create your own custom plugins.
</link-summary>

Ktor allows you to create your own custom [plugins](server-plugins.md). In general, this API doesn't
require an understanding of internal Ktor concepts, such as pipelines and phases. Instead, you use handlers such as
`onCall()`, `onCallReceive()`, and `onCallRespond()` to access different stages of [requests and response handling](#call-handling).

## Create and install your first plugin {id="first-plugin"}

In this section, you'll learn how to create and install your first plugin.

You can use an application created in the [](server-create-a-new-project.topic) tutorial as a starting project.

1. To create a plugin, call
   the [`createApplicationPlugin()`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/create-application-plugin.html)
   function and specify a plugin name:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt" include-lines="3-7"}

   This function returns the `ApplicationPlugin` instance that you can install in your application.
   
   > You can also use the
   > [`createRouteScopedPlugin()`](https://api.ktor.io/ktor-server-core/io.ktor.server.application/create-route-scoped-plugin.html)
   > function to create a plugin that can be [installed on a specific route](server-plugins.md#install-route).
   >
   {style="tip"}

2. To [install a plugin](server-plugins.md#install), pass the created `ApplicationPlugin` instance to the `Application.install()` function in
   your application's initialization code:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="11-12,32"}

3. [Run](server-run.md) your application to see the plugin message in the console output:

   ```Bash
   2021-10-14 14:54:08.269 [main] INFO  Application - Autoreload is disabled because the development mode is off.
   SimplePlugin is installed!
   2021-10-14 14:54:08.900 [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```

> For the full example, see [SimplePlugin.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt).
> 
{style="tip"}

## Handle calls {id="call-handling"}

In your custom plugin, you can [handle requests](server-requests.md) and [responses](server-responses.md) by using a set
of handlers that provide access to different stages of a call:

* [`onCall()`](#on-call) allows you to access request and response information and modify response parameters, such as
  headers.
* [`onCallValidators()`](#on-call-validators) allows you perform call validation. For route-scoped plugins, validators
  execute according to route nesting.
* [`onCallReceive()`](#on-call-receive) allows you to transform data received from the client.
* [`onCallRespond()`](#on-call-respond) allows you to transform data before sending it to the client.
* [`on()`](#other) allows you to handle specific hooks for other stages of call processing or for
  exceptions that occur during a call.

You can also [share call state](#call-state) between handlers using `call.attributes`.

### `onCall()` {id="on-call"}

The `onCall()` handler accepts `ApplicationCall` as a lambda argument. This allows you to access request and response
information and modify response parameters, such as [appending custom headers](#custom-header).

To transform a request or response body, use [`onCallReceive()`](#on-call-receive) and [`onCallRespond()`](#on-call-respond).

#### Example 1: Log requests {id="request-logging"}

The following example uses `onCall()` to create a plugin that logs incoming request URLs:

```kotlin
```

{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt" include-lines="6-12"}

When you install this plugin, it prints requested URLs to the console:

```Bash
Request URL: http://0.0.0.0:8080/
Request URL: http://0.0.0.0:8080/index
```

#### Example 2: Add a custom header {id="custom-header"}

The following example creates a plugin that adds a custom header to each response:

```kotlin
val CustomHeaderPlugin = createApplicationPlugin(name = "CustomHeaderPlugin") {
    onCall { call ->
        call.response.headers.append("X-Custom-Header", "Hello, world!")
    }
}
```

The resulting response includes the custom header:

```HTTP
HTTP/1.1 200 OK
X-Custom-Header: Hello, world!
```

In this example, the header name and value are hardcoded. To make them configurable, provide a 
[plugin configuration](#plugin-configuration).

### `onCallReceive()` {id="on-call-receive"}

The `onCallReceive()` handler allows you to transform data received from the client. Inside the handler, call `transformBody()`
to transform the request body before it is passed to `call.receive()`.

Suppose a client sends the following `POST` request that contains `10` as a `text/plain` body:

```HTTP
```

{src="snippets/custom-plugin/post.http"}

To [receive this body](server-requests.md#objects) as an integer value, you need to create a route handler for `POST`
requests and call `call.receive()` with the `Int` parameter:

```kotlin
```

{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="27-28,30"}

The following plugin receives a body as an integer value and adds `1` to it:

```kotlin
```

{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" include-lines="6-16,27"}

In the above example:

* `TransformBodyContext` is
   the [lambda receiver](https://kotlinlang.org/docs/scope-functions.html#context-object-this-or-it). Its `requestedType` property contains information about the type requested by `call.receive()`.
* The `data` argument contains the current request body. In this case, it is a [`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html)
   and `ByteReadChannel.readLine()` reads its contents.
* If the requested type is `Int`, the plugin converts the received value to an integer, adds `1`, and returns the
   transformed value. Otherwise, it returns the body unchanged.

> For the full example, see [DataTransformationPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt).
>
{style="tip"}

### `onCallRespond()` {id="on-call-respond"}

The `onCallRespond()` handler allows you to transform data before it is sent to the client.
This handler is executed when the `call.respond` function is invoked in a route handler.

For example, consider the following route:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="27-30"}

Calling `call.respond` invokes `onCallRespond()`, which in turn allows you to transform data to be sent to the client.

Inside `onCallRespond()`, use `transformBody()` to transform the response body. The following example adds `1` to an
integer response and converts it to a string:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt" include-lines="18-26"}

> For the full example, see [DataTransformationPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt).
>
{style="tip"}

### `onCallValidators()` {id="on-call-validators"}

The `onCallValidators()` handler allows you to perform validation for each incoming call.

When multiple validators are applied to nested routes, validators on parent routes execute before validators on child
routes. This allows a validator to use information produced earlier in the route hierarchy, such as an authenticated
principal.

For example, the following route-scoped plugin can access a principal provided by an authentication route:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/UserValidationPlugin.kt" include-symbol="UserValidationPlugin"}

Install the plugin inside the authenticated route to run it after authentication:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="20,31-37"}

Use `onCallValidators()` when the order of route-scoped validation matters. For general request and response processing
that does not depend on other validators, use `onCall()` instead.

### Other useful handlers {id="other"}

In addition to the call handlers described above, Ktor provides a set of hooks for handling other stages of call
processing. Use the `on()` function to register a handler for a specific `Hook`.

Available hooks include:

- `CallSetup` is invoked at the beginning of call processing.
- `ResponseBodyReadyForSend` is invoked after a response body comes through all transformations and is ready to be sent.
- `ResponseSent` is invoked after a response is successfully sent to a client.
- `CallFailed` is invoked when call processing fails with an exception.
- [`AuthenticationChecked`](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-authentication-checked/index.html)
  is invoked after [authentication](server-auth.md) credentials are checked. You can use this hook to implement
  authorization. For an example, see [custom-plugin-authorization](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin-authorization).

The following example handles the `CallSetup` hook:

```kotlin
on(CallSetup) { call->
    // ...
}
```

> You can also use the `MonitoringEvent` to [handle application events](#handle-app-events), such as
> application startup or shutdown.
> 
{style="tip"}

### Share call state {id="call-state"}

Custom plugins can share values associated with a call between different handlers.
These values are stored in the `call.attributes` collection using a unique `AttributeKey`.

The following example stores the time when `onCall()` is invoked and uses it in `onCallReceive()` to calculate the delay
before the request body is read:

```kotlin
```
{src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationBenchmarkPlugin.kt"
include-lines="6-18"}

When you send a `POST` request, the plugin prints the delay to the console:

```Bash
Request URL: http://localhost:8080/transform-data
Read body delay (ms): 52
```

> Fo the full example, see [DataTransformationBenchmarkPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationBenchmarkPlugin.kt).
>
{style="tip"}

> You can also access call attributes from a [route handler](server-requests.md#request_information).
> 
{style="tip"}

## Handle application events {id="handle-app-events"}

The [`on()`](#other) handler provides the ability to use the `MonitoringEvent` hook to handle events related to an
application's lifecycle.

Ktor provides the following [predefined events](server-events.md#predefined-events) to the `on()` handler:

- `ApplicationStarting`
- `ApplicationStarted`
- `ApplicationStopPreparing`
- `ApplicationStopping`
- `ApplicationStopped`

The following example handles application shutdown using the `ApplicationStopped` event:

```kotlin
```

{src="snippets/events/src/main/kotlin/com/example/plugins/ApplicationMonitoringPlugin.kt" lines="12-13,17"}

This approach is useful for cleaning up resources owned by a plugin, such as closing connections, stopping background
tasks, or flushing buffered data.

## Provide plugin configuration {id="plugin-configuration"}

The [custom header](#custom-header) example creates a plugin that appends a predefined header to each response.
To make this plugin reusable, define a configuration that lets users specify the header name and value.

1. Define a configuration class:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt" include-lines="18-21"}

2. Pass the configuration class reference to `createApplicationPlugin()`:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt" include-lines="5-16"}

   Plugin configuration properties are mutable during plugin installation. If the plugin uses these values in handlers,
   store them in local variables inside the plugin body.

3. Install and configure the plugin:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="15-18"}

> For the full example, see [CustomHeaderPlugin.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt).
>
{style="tip"}

### Configuration in a file {id="configuration-file"}

Ktorcan load plugin settings from a [configuration file](server-create-and-configure.topic#engine-main).

The following example shows how to configure `CustomHeaderPlugin` from a file.

1. Add a new group with the plugin settings to your `application.conf` or `application.yaml` file:

   <tabs group="config">
   <tab title="application.conf" group-key="hocon">

   ```shell
   ```
   {src="snippets/custom-plugin/src/main/resources/application.conf" include-lines="10-15"}

   </tab>
   <tab title="application.yaml" group-key="yaml">

   ```yaml
   ```
   {src="snippets/custom-plugin/src/main/resources/application.yaml" include-lines="8-11"}

   </tab>
   </tabs>

   In this example, the plugin settings are stored in the `http.custom_header` group.

2. To get access to configuration file properties, pass `ApplicationConfig` to the configuration class constructor.
   The `tryGetString()` function returns the value of the specified property:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPluginConfigurable.kt"
   include-lines="20-23"}

3. Assign the `http.custom_header` value to the `configurationPath` parameter of the `createApplicationPlugin()`
   function:

   ```kotlin
   ```
   {src="snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPluginConfigurable.kt"
   include-lines="6-18"}

> For the full example, see [CustomHeaderPluginConfigurable.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPluginConfigurable.kt).
>
{style="tip"}

## Access application settings {id="app-settings"}

Custom plugins can access application-level settings from the plugin body. This is useful when plugin behavior depends on
the server configuration or environment.

### Configuration {id="config"}

Use the `applicationConfig` property to access server configuration. This property returns an
[`ApplicationConfig`](https://api.ktor.io/ktor-server-core/io.ktor.server.config/-application-config/index.html)
instance.

The following example reads the host and port used by the server:

```kotlin
val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
   val host = applicationConfig?.host
   val port = applicationConfig?.port
   println("Listening on $host:$port")
}
```

### Environment {id="environment"}

Use the `environment` property to access the application's environment. For example, you can check whether
[development mode](server-development-mode.topic) is enabled:

```kotlin
val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
   val isDevMode = environment?.developmentMode
   onCall { call ->
      if (isDevMode == true) {
         println("handling request ${call.request.uri}")
      }
   }
}
```

## Miscellaneous {id="misc"}

### Store plugin state {id="plugin-state"}

A plugin can store state by capturing values in the plugin body and using them from handler lambdas.

Because plugins can handle multiple calls concurrently, store shared mutable state in thread-safe structures, such as
concurrent collections or atomic types:

```kotlin
val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
   val activeRequests = AtomicInteger(0)
   onCall {
      activeRequests.incrementAndGet()
   }
   onCallRespond {
      activeRequests.decrementAndGet()
   }
}
```

### Databases {id="databases"}

#### Use suspending database APIs

All custom plugin handlers are suspending functions. This means you can call suspending database APIs directly from a
handler.

Remember to release resources that are scoped to a specific call. For example, you can use [`on(ResponseSent)`](#other) to
clean up resources after a response has been sent.

#### Use blocking database APIs

Ktor uses coroutines, so blocking database calls should not run on the default coroutine dispatcher. A blocking call can
occupy a thread and prevent other coroutines from progressing.

To call a blocking database API, create a separate
[`CoroutineContext`](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) for blocking work:

```kotlin
val databaseContext = Dispatchers.IO
```

Then wrap each blocking database call in `withContext()`:

```kotlin
onCall {
   withContext(databaseContext) {
       database.access(...) // A call to your database
   }
}
```
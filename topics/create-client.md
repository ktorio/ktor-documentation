[//]: # (title: Creating and configuring a client)

<excerpt>Learn how to create and configure the Ktor client.</excerpt>

To instantiate the client, create the [HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html) class instance and pass an engine as a parameter:

```kotlin
```
{src="snippets/_misc_client/CioCreate.kt"}

In this example, we use the [CIO](https://api.ktor.io/ktor-client/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o/index.html) engine.
You can also omit an engine:

```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

In this case, the client will choose an engine automatically depending on the artifacts [added in a build script](client-dependencies.md#engine-dependency). You can learn how the client chooses an engine from the [](http-client_engines.md#default) documentation section.

## Configure the client {id="configure-client"}

### Basic configuration {id="basic-config"}

To configure the client, you can pass an additional functional parameter to the client constructor. The [HttpClientEngineConfig](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.engine/-http-client-engine-config/index.html) class is a base class for configuring the client. For instance, we can change the behaviour of exceptions as follows:

```kotlin
```
{src="snippets/_misc_client/BasicClientConfig.kt"}

### Engine configuration {id="engine-config"}
You can configure an engine using the `engine` function:

```kotlin
```
{src="snippets/_misc_client/BasicEngineConfig.kt"}

See the [Engines](http-client_engines.md) section for additional details.

### Plugins {id="plugins"}
To install a plugin, you need pass it to the `install` function inside a [client configuration block](#configure-client). For example, you can log HTTP calls by installing the [Logging](client_logging.md) plugin:

```kotlin
```
{src="snippets/_misc_client/InstallLoggingPlugin.kt"}

You can also configure a plugin inside the `install` block. For example, for the [Logging](client_logging.md) plugin, you can specify the logger and logging level:
```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" lines="12-17"}

## Use the client {id="use-client"}
After you've [added](client-dependencies.md) all the required dependencies and created the client, you can use it to [make requests](request.md) and [receive responses](response.md). 


## Close the client {id="close-client"}

After you finish working with the HTTP client, you need to free up the resources: threads, connections, and `CoroutineScope` for coroutines. To do this, call the `HttpClient.close` function:

```kotlin
client.close()
```

If you need to use `HttpClient` for a single request, call the `use` function, which automatically calls `close` after executing the code block:

```kotlin
val status = HttpClient().use { client ->
    // ...
}
```

Note that the `close` function prohibits creating new requests but doesn't terminate currently active ones. Resources will only be released after all client requests are completed.
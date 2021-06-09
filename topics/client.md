[//]: # (title: Setting up a client)

Ktor includes a multiplatform asynchronous HTTP client, which allows you to make requests and handle responses, extend its functionality with plugins (formerly known as features), such as authentication, JSON serialization, and so on.
In this topic, we'll take an overview of the client - from setting it up to making requests and installing plugins. 

## Add dependencies {id="add-dependencies"}
To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an [engine](http-client_engines.md) dependency. If you want to extend the client functionality with specific [plugins](http-client_plugins.md), you also need to add the appropriate dependencies.

### Client dependency {id="client-dependency"}
The main client functionality is available in the `ktor-client-core` artifact. Depending on your build system, you can add it in the following way:
<var name="artifact_name" value="ktor-client-core"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


### Engine dependency {id="engine-dependency"}
An engine is responsible for processing network requests. There are different client engines available for [various platforms](http-client_multiplatform.md), such as Apache, CIO, Android, iOS, and so on. For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

For a full list of dependencies required for a specific engine, see [](http-client_engines.md#dependencies).

### Plugin dependency {id="feature-dependency"}
If you want to use additional client plugins, you need to add a corresponding dependency. You can learn which dependencies you need from a topic for a required plugin.




## Create the client {id="create-client"}

To instantiate the client, create the [HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client/-http-client/index.html) class instance and pass an engine as a parameter:

```kotlin
```
{src="snippets/_misc_client/CioCreate.kt"}

In this example, we use the [CIO](https://api.ktor.io/ktor-client/ktor-client-cio/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o/index.html) engine. 

You can also omit an engine:

```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

In this case, the client will choose an engine automatically depending on the artifacts [added in a build script](#engine-dependency). You can learn how the client chooses an engine from the [](http-client_engines.md#default) documentation section.

## Configure the client {id="configure-client"}

### Basic configuration {id="basic-config"}

To configure the client, you can pass an additional functional parameter to the client constructor. The [HttpClientEngineConfig](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.engine/-http-client-engine-config/index.html) class is a base class for configuring the client. For instance, we can change the behaviour of exceptions as follows:

```kotlin
```
{src="snippets/_misc_client/BasicClientConfig.kt"}

### Engine configuration {id="engine-config"}
You can configure an engine using the `engine` function:

```kotlin
```
{src="snippets/_misc_client/BasicEngineConfig.kt"}

See the [Engines](http-client_engines.md) section for additional details.

### Plugins {id="features"}

Ktor lets you use additional client functionality (plugins, formerly known as features) that is not available by default, for example, logging, authorization, or serialization. Most of them are provided in separate artifacts. For example, you can log HTTP calls by installing the [Logging](client_logging.md) plugin:

```kotlin
```
{src="snippets/_misc_client/InstallLoggingFeature.kt"}

You can also configure a plugin inside the `install` block. For example, for the [Logging](client_logging.md) plugin, you can specify the logger and logging level:
```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" lines="12-17"}

## Make a request {id="make-request"}

The main way for making HTTP requests is the [request](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.request/request.html) function that takes a URL as a parameter. Inside this function, you can configure various request parameters: specify an HTTP method, add headers, specify the request body, and so on. These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html) class.

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithoutParams.kt" interpolate-variables="true" disable-links="false"}

Note that this function allows you to [receive a response](#response) in various ways. In this example, we receive a response as an `HttpResponse` object.

> `request` is a suspending function, so requests should be executed only from a coroutine or another suspend function. You can learn more about calling suspending functions from [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).

When calling the `request` function, you can specify the desired HTTP method using the `method` property:

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithParams.kt"}

In addition to the `request` function, `HttpClient` provides specific functions for basic HTTP methods: `get`, `post`, `put`, and so on. For example, you can replace the example above with the following code:
```kotlin
```
{src="snippets/_misc_client/GetMethodWithoutParams.kt"}

To learn how to add headers, cookies, and specify a request body, see the [](request.md) help topic.



### Receive a response {id="response"}
All functions used to [make an HTTP request](request.md) (`request`, `get`, `post`, etc.) allow you to receive a response as an [HttpResponse](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.statement/-http-response/index.html) object. `HttpResponse` exposes API required to get a response body in various ways (raw bytes, JSON objects, etc.) and obtain response parameters, such as a status code, content type, headers, and so on. For example, you can receive a body as [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/) as follows:

```kotlin
```
{src="snippets/_misc_client/ResponseTypes.kt" lines="11,13"}

You can learn more from the [](response.md) topic.

> With the [JSON plugin](json.md#receive_data) installed, you can deserialize JSON content into a custom data object.


## Close the HTTP client {id="close-client"}

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

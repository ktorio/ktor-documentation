[//]: # (title: Set up a Client)

Ktor includes a multiplatform asynchronous HTTP client, which allows you to make requests and handle responses, extend its functionality with features, such as authentication, JSON serialization, and so on.
In this topic, we'll take an overview of the client - from setting it up to making requests and installing features. 

## Add Dependencies {id="add-dependencies"}
To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an [engine](http-client_engines.md) dependency. If you want to extend the client functionality with specific [features](http-client_features.md), you also need to add the appropriate dependencies.

### Client Dependency {id="client-dependency"}
The main client functionality is available in the `ktor-client-core` artifact. Depending on your build system, you can add it in the following way:
<var name="artifact_name" value="ktor-client-core"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


### Engine Dependency {id="engine-dependency"}
An engine is responsible for processing network requests. There are different client engines available for [various platforms](http-client_multiplatform.md), such as Apache, CIO, Android, iOS, and so on. For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

For a full list of dependencies required for a specific engine, see [](http-client_engines.md#dependencies).

### Feature Dependency {id="feature-dependency"}
If you want to use additional client features, you need to add a corresponding dependency. You can learn which dependencies you need from a topic for a required feature.




## Create the Client {id="create-client"}

To instantiate the client, create the [HttpClient](https://api.ktor.io/%ktor_version%/io.ktor.client/-http-client/index.html) class instance and pass an engine as a parameter:

```kotlin
```
{src="snippets/_misc_client/CioCreate.kt"}

In this example, we use the [CIO](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o/index.html) engine. 

You can also omit an engine:

```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

In this case, the client will choose an engine automatically depending on the artifacts [added in a build script](#engine-dependency). You can learn how the client chooses an engine from the [](http-client_engines.md#default) documentation section.

## Configure the Client {id="configure-client"}

### Basic Configuration {id="basic-config"}

To configure the client, you can pass an additional functional parameter to the client constructor. The [HttpClientEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html) class is a base class for configuring the client. For instance, we can change the behaviour of exceptions as follows:

```kotlin
```
{src="snippets/_misc_client/BasicClientConfig.kt"}

### Engine Configuration {id="engine-config"}
You can configure an engine using the `engine` function:

```kotlin
```
{src="snippets/_misc_client/BasicEngineConfig.kt"}

See the [Engines](http-client_engines.md) section for additional details.

### Features {id="features"}

Ktor lets you use additional client functionality (features) that is not available by default, for example, logging, authorization, or serialization. Most of them are provided in separate artifacts. For example, you can log HTTP calls by installing the [Logging](features_logging.md) feature:

```kotlin
```
{src="snippets/_misc_client/InstallLoggingFeature.kt"}

You can also configure a feature inside the `install` block. For example, for the [Logging](features_logging.md) feature, you can specify the logger and logging level:
```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" lines="12-17"}

## Make a Request {id="make-request"}

The main way for making HTTP requests is the [request](https://api.ktor.io/%ktor_version%/io.ktor.client.request/request.html) function that takes a URL as a parameter. Inside this function, you can configure various request parameters: specify an HTTP method, add headers, specify the request body, and so on. These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/index.html) class.

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



### Receive a Response {id="response"}
All request functions (`request`, `get`, `post`, etc.) allow you to receive a response in several ways:
* As an [HttpResponse](https://api.ktor.io/%ktor_version%/io.ktor.client.statement/-http-response/index.html) object:
   ```kotlin
   ```
  {src="snippets/_misc_client/ResponseTypes.kt" include-symbol="httpResponse"}
  
* As a string:
   ```kotlin
   ```
  {src="snippets/_misc_client/ResponseTypes.kt" include-symbol="stringResponse"}
  
* As a byte array:
   ```kotlin
   ```
  {src="snippets/_misc_client/ResponseTypes.kt" include-symbol="byteArrayResponse"}

You can learn more from the [](response.md) topic.

> With the [JSON feature](json-feature.md#receive_data) installed, you can deserialize JSON content into a custom data object.


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

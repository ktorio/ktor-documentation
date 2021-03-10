[//]: # (title: Client Overview)

Ktor includes a multiplatform asynchronous HTTP client, which allows you to make requests and handle responses, extend its functionality with features, such as authentication, JSON serialization, and so on.
In this topic, we'll take an overview of the client - from setting it up to making requests and installing features. 

## Add Dependencies {id="add-dependencies"}
To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an [engine](http-client_engines.md) dependency. If you want to extend the client functionality with specific [features](http-client_features.md), you also need to add the appropriate dependencies.

### Client Dependency {id="client-dependency"}
The main client functionality is available in the `ktor-client-core` artifact. Depending on your build system, you can add it in the following way:
<var name="artifact_name" value="ktor-client-core"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


### Engine Dependency {id="engine-dependency"}
An engine is in charge of processing network requests. There are different client engines available for [various platforms](http-client_multiplatform.md), such as Apache, CIO, Android, iOS, and so on. For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

For a full list of dependencies required for a specific engine, see [](http-client_engines.md#dependencies).

### Feature Dependency {id="feature-dependency"}
If you want to use additional client features, you need to add a corresponding dependency. You can learn which dependencies you need from a topic for a required feature.




## Create the Client {id="create-client"}

To instantiate the client, create the [HttpClient](https://api.ktor.io/%ktor_version%/io.ktor.client/-http-client/index.html) class instance and pass an engine as a parameter:

```kotlin
val client = HttpClient(CIO)
```

In this example, we use the [CIO](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o/index.html) engine. You can also omit an engine:

```kotlin
val client = HttpClient()
```

In this case, the client will choose an engine automatically depending on the artifacts [added in a build script](#engine-dependency). You can learn more from the [Engines](http-client_engines.md#default) topic.

## Configure the Client {id="configure-client"}

### Basic Configuration {id="basic-config"}

To configure the client, you can pass an additional functional parameter to the client constructor. The [HttpClientEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html) class is a base class for configuring the client. In the example below, receiving HTTP errors in response don't cause exceptions:

```kotlin
val client = HttpClient(CIO) {
    expectSuccess = false
}
```

### Engine Configuration {id="engine-config"}
You can configure an engine using the `engine` method in a block:

```kotlin
val client = HttpClient(CIO) {
    engine {
        // Configure an engine
    }
}
```

See [Engines](http-client_engines.md) section for additional details.

### Features {id="features"}

Ktor lets you use additional client functionality (features) that is not available by default, for example, logging, authorization, or serialization. Most of them are distributed in separate artifacts. For example, you can log HTTP calls by installing the [Logging](features_logging.md) feature:
```kotlin
val client = HttpClient(CIO) {
    install(Logging)
}
```
You can also configure a feature inside the `install` block. For example, for the `Logging` feature, you can specify the logger and logging level:
```kotlin
```
{src="/snippets/client-logging/src/main/kotlin/com/example/Application.kt" include-symbol="client"}

## Make a Request {id="make-request"}

The main way for making HTTP requests is the [request](https://api.ktor.io/%ktor_version%/io.ktor.client.request/request.html) function that takes URL as a parameter. Inside this function, you can configure various request parameters: specify an HTTP method, add headers, specify the request body, and so on. These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/index.html) class.
```kotlin
val response: HttpResponse = client.request("https://ktor.io/") {
    // Configure request parameters exposed by HttpRequestBuilder
}
```
Note that this function allows you to [receive a response](#response) in various ways. In this example, we receive a response as an `HttpResponse` object.

> `request` is a suspending function, so requests should be executed only from a coroutine or another suspend function. You can learn more about calling suspending functions from [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).

### Specify an HTTP Method {id="http-method"}

When calling the `request` function, you can specify the desired HTTP method using the `method` property:

```kotlin
val response: HttpResponse = client.request("https://ktor.io/") {
    method = HttpMethod.Get
}
```

In addition to the `request` function, `HttpClient` provides specific functions for basic HTTP methods: `get`, `post`, `put`, and so on. For example, you can replace the example above with the following code:
```kotlin
val response: HttpResponse = client.get("https://ktor.io/")
```

### Add Headers {id="headers"}
To add headers to the request, use the `headers` function as follows:
```kotlin
val response: HttpResponse = client.get("https://ktor.io/") {
    headers {
        append(HttpHeaders.Accept, "text/html")
        append(HttpHeaders.Authorization, "token")
        append(HttpHeaders.UserAgent, "ktor client")
    }
}
```


### Specify Body {id="body"}
To set the body of a request, assign a value to the `body` property. You can assign a string or an [OutgoingContent](https://api.ktor.io/%ktor_version%/io.ktor.http.content/-outgoing-content/index.html) object to this property. For example, sending data with a `text/plain` text MIME type can be implemented as follows:
```kotlin
val response: HttpResponse = client.post("http://127.0.0.1:8080/") {
    body = TextContent(
        text = "Body content",
        contentType = ContentType.Text.Plain
    )
}
```


### Receive a Response {id="response"}
Functions used to make a request (`request`, `get`, `post`, etc.) allow you to receive a response in several ways:
* As an [HttpResponse](https://api.ktor.io/%ktor_version%/io.ktor.client.statement/-http-response/index.html) object:
   ```kotlin
   val httpResponse: HttpResponse = client.get("https://ktor.io/")
   ```
* As a string:
   ```kotlin
      val stringResponse: String = client.get("https://ktor.io/")
   ```
* As a byte array:
   ```kotlin
      val byteArrayResponse: ByteArray = client.get("https://ktor.io/")
   ```

You can learn more from the [](response.md) topic.

> With the [JSON feature](json-feature.md#receive_data) installed, you can deserialize JSON content into a custom data object.


## Close the HTTP client {id="close-client"}

After you finish working with the HTTP client, you need to free up the resources: threads, connections, and `CoroutineScope` for coroutines. To do this, call up the `close()` function in `HttpClient`:

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

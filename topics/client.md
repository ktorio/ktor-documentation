[//]: # (title: Client Overview)

<include src="lib.md" include-id="outdated_warning"/>

In addition to HTTP serving, Ktor also includes a flexible asynchronous HTTP client.
This client supports several [configurable engines](http-client_engines.md), and has its own set of [features](http-client_features.md).

>The main functionality is available through the `io.ktor:ktor-client-core:$ktor_version` artifact.
>And each engine, is provided in [separate artifacts](http-client_engines.md).
>
{type="note"}

## Adding an engine dependency

The first thing you need to do before using the client is to add a client engine dependency. Client engine is a request executor that performs requests from ktor API. There are many client engines for each platform available out of the box: [`Apache`](http-client_engines.md#apache),
[`OkHttp`](http-client_engines.md#okhttp),
[`Android`](http-client_engines.md#android),
[`Ios`](http-client_engines.md#ios),
[`Js`](http-client_engines.md#js-javascript),
[`Jetty`](http-client_engines.md#jetty),
[`CIO`](http-client_engines.md#cio) and [`Mock`](http-client_engines.md#mock). For a full list of dependencies required for a specific engine, see [](http-client_engines.md#dependencies). 

For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Creating client

Next you can create a client as here:

```kotlin
val client = HttpClient(CIO)
```

where `CIO` is engine class here. If you confused which engine class you should use consider using `CIO`.

If you're using multiplatform, you can omit the engine:

```kotlin
val client = HttpClient()
```

Ktor will choose an engine among the ones that are available from the included artifacts using a `ServiceLoader` on the JVM, or similar approach in the other platforms. If there are multiple engines in the dependencies Ktor chooses first in alphabetical order of engine name.

It's safe to create multiple instance of client or use the same client for multiple requests.

## Releasing resources

Ktor client is holding resources: prepared threads, coroutines and connections. After you finish working with the client, you may wish to release it by calling `close`:

```kotlin
client.close()
```

If you want to use a client to make only one request consider `use`-ing it. The client will be automatically closed once the passed block has been executed:

```kotlin
val status = HttpClient().use { client ->
    ...
}
```

The method `close` signals to stop executing new requests. It wouldn't block and allows all current requests to finish successfully and release resources. You can also wait for closing with the `join` method or halt any activity using the `cancel` method. For example:

```kotlin
try {
    // Close and wait for 3 seconds.
    withTimeout(3000) {
        client.close()
        client.join()
    }
} catch (timeout: TimeoutCancellationException) {
    // Cancel after timeout
    client.cancel()
}
```

Ktor HttpClient follows `CoroutineScope` lifecycle. Check out [Coroutines guide](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/) to learn more.

## Client configuration

To configure the client you can pass additional functional parameter to client constructor. The client configured with [HttpClientEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html).

For example, you can limit `threadCount` or setup [proxy](proxy.md):

```kotlin
val client = HttpClient(CIO) {
    threadCount = 2
}
```

You also can configure an engine using the `engine` method in a block:

```kotlin
val client = HttpClient(CIO) {
    engine {
        // engine configuration
    }
}
```

See [Engines](http-client_engines.md) section for additional details.

Proceed to [Preparing the request](request.md).


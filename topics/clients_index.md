[//]: # (title: Client)

<include src="lib.xml" include-id="outdated_warning"/>

In addition to HTTP serving, Ktor also includes a flexible asynchronous HTTP client.
This client supports several [configurable engines](http-client_engines.md), and has its own set of [features](http-client_plugins.md).

>The main functionality is available through the `io.ktor:ktor-client-core:$ktor_version` artifact.
>And each engine, is provided in [separate artifacts](http-client_engines.md).
>
{type="note"}

## Calls: Requests and Responses
{id="requests-responses"}

You can check [how to make requests](request.md),
and [how to receive responses](response.md) in their respective sections.

## Concurrency

Remember that requests are asynchronous, but when performing requests, the API suspends further requests
and your function will be suspended until done. If you want to perform several requests at once
in the same block, you can use `launch` or `async` functions and later get the results.
For example:

### Sequential requests

```kotlin
suspend fun sequentialRequests() {
    val client = HttpClient()

    // Get the content of an URL.
    val firstBytes = client.get<ByteArray>("https://127.0.0.1:8080/a")

    // Once the previous request is done, get the content of an URL.
    val secondBytes = client.get<ByteArray>("https://127.0.0.1:8080/b")

    client.close()
}
```

### Parallel requests

```kotlin
suspend fun parallelRequests() = coroutineScope<Unit> {
    val client = HttpClient()

    // Start two requests asynchronously.
    val firstRequest = async { client.get<ByteArray>("https://127.0.0.1:8080/a") }
    val secondRequest = async { client.get<ByteArray>("https://127.0.0.1:8080/b") }

    // Get the request contents without blocking threads, but suspending the function until both
    // requests are done.
    val bytes1 = firstRequest.await() // Suspension point.
    val bytes2 = secondRequest.await() // Suspension point.

    client.close()
}
```

## Examples
{id="examples"}

For more information, check the [examples page](examples.md) with some examples.

## Features
{id="features"}

For more information, check the [features page](http-client_plugins.md) with all the available features.

[//]: # (title: Streaming)
[//]: # (caption: Handling streaming data)
[//]: # (category: clients)
[//]: # (permalink: /clients/http-client/quick-start/streaming.html)
[//]: # (redirect_from: redirect_from)
[//]: # (- /clients/http-client/calls/streaming.html: - /clients/http-client/calls/streaming.html)
[//]: # (ktor_version_review: 1.3.0)

Most of the response types are complete in memory. But you can also fetch streaming data as well.

## Scoped streaming

There are multiple ways of doing streaming. The safest way is using [HttpStatement](https://api.ktor.io/%ktor_version%/io.ktor.client.statement/-http-statement/) with scoped `execute` block:

```kotlin
client.get<HttpStatement>.execute { response: HttpResponse ->
    // Response is not downloaded here.
    val channel = response.receive<ByteReadChannel>()
}
```

After `execute` block is finished, network resources is released.

You can also point different type for `execute` method:

```kotlin
client.get<HttpStatement>.execute<ByteReadChannel> { channel: ByteReadChannel ->
    // ...
}
```
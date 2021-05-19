[//]: # (title: Client WebSockets)

<include src="lib.md" include-id="outdated_warning"/>

Ktor provides Websocket client support for the following engines: CIO, OkHttp, Js. To get more information about 
the server side, follow this [section](websocket.md).

Once connected, client and server WebSockets share the same [WebSocketSession](websocket.md#WebSocketSession)
interface for communication.


## Add dependencies {id="add_dependencies"}
To use `WebSockets`, you need to include the `ktor-client-websockets` artifact in the build script:

<var name="artifact_name" value="ktor-client-websockets"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Usage {id="usage"}

The basic usage to create an HTTP client supporting WebSockets is pretty simple:

```kotlin
val client = HttpClient {
    install(WebSockets)
}
```

Once created we can perform a request, starting a `WebSocketSession`:

```kotlin
client.ws(
    method = HttpMethod.Get,
    host = "127.0.0.1",
    port = 8080, path = "/route/path/to/ws"
) { // this: DefaultClientWebSocketSession

    // Send text frame.
    send("Hello, Text frame")

    // Send text frame.
    send(Frame.Text("Hello World"))

    // Send binary frame.
    send(Frame.Binary(...))

    // Receive frame.
    val frame = incoming.receive()
    when (frame) {
        is Frame.Text -> println(frame.readText())
        is Frame.Binary -> println(frame.readBytes())
    }
}
```

For more information about the WebSocketSession, check the [WebSocketSession page](websocket.md#WebSocketSession) and the [API reference](https://api.ktor.io/%ktor_version%/io.ktor.client.features.websocket/).
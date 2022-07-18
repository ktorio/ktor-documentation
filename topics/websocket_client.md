[//]: # (title: Client WebSockets)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-websockets</code>
</p>
<var name="example_name" value="client-websockets"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Ktor supports the WebSocket protocol and allows you to create applications that require real-time data transfer from and to the server.
</link-summary>

Ktor supports the WebSocket protocol and allows you to create applications that require real-time data transfer from and to the server. For example, WebSockets can be used to create a [chat application](getting_started_ktor_client_chat.md).

The Ktor client allows you to handle a WebSocket session for exchanging messages with the server. To learn about WebSocket support in a Ktor server, see [](websocket.md).

## Supported engines {id="supported_engines"}

The Ktor client supports WebSockets for the following [engines](http-client_engines.md):
- CIO
- OkHttp
- Js
- Darwin


## Add dependencies {id="add_dependencies"}
To use WebSockets, you need to include the `ktor-client-websockets` artifact in the build script:

<var name="artifact_name" value="ktor-client-websockets"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install WebSockets {id="install_plugin"}
To install the `WebSockets` plugin, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    install(WebSockets) {
        // Configure WebSockets
    }
}
```

Optionally, you can configure the plugin inside the `install` block by passing the required options using [WebSockets.Config](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/-web-sockets/-config/index.html).


## Handle a WebSockets session {id="handle-session"}
### API overview {id="api-overview"}

A client's WebSocket session is represented by the [DefaultClientWebSocketSession](https://api.ktor.io/ktor-shared/ktor-websockets/io.ktor.websocket/-default-web-socket-session/index.html) interface. This interface exposes the API that allows you to send/receive WebSocket frames and close a session. `HttpClient` allows you to get access to a WebSocket session in one of the following ways:

- The [webSocket](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/web-socket.html) function accepts `DefaultClientWebSocketSession` as a block argument.
  ```kotlin
     runBlocking {
         client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/echo") {
             // this: DefaultClientWebSocketSession
         }
     }
  ```
- The [webSocketSession](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/web-socket-session.html) function returns the `DefaultClientWebSocketSession` instance and allows you to access a session outside the `runBlocking` or `launch` scope.

Session configuration might look as follows:

1. Use the `send` function to send text content to the server.
2. Use the `incoming` and `outgoing` properties to access the channels for receiving and sending WebSocket frames. A frame is represented by the `Frame` class.
3. When handling a session, you can check a frame type, for example:
    * `Frame.Text` is a text frame. For this frame type, you can read its content using `Frame.Text.readText()`.
    * `Frame.Binary` is a binary frame. For this type, you can read its content using `Frame.Binary.readBytes()`.
    * `Frame.Close` is a closing frame. You can call `Frame.Close.readReason()` to get a close reason for the current session.
4. Use the `close` function to send a close frame with the specified reason.

### Example {id="example"}

The example below shows how to send a message to the `echo` WebSocket endpoint and how to receive a response:

```kotlin
```
{src="snippets/client-websockets/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

You can find the full example here: [client-websockets](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-websockets).

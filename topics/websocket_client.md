[//]: # (title: WebSockets)

<show-structure for="chapter" depth="2"/>

<var name="example_name" value="client-websockets"/>
<var name="artifact_name" value="ktor-client-websockets"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-websockets</code>
</p>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The Websockets plugin allows you to create a multi-way communication session between a server and a client.
</link-summary>

<include from="websocket.md" element-id="websockets-description"></include>

The Websockets plugin for the client allows you to handle a WebSocket session for exchanging messages with a server.


> Not all engines support WebSockets. For an overview of the supported engines, refer
to [](http-client_engines.md#limitations).
>
{style="note"}


> To learn about WebSocket support on the server-side, see [](websocket.md).

## Add dependencies {id="add_dependencies"}

To use `WebSockets`, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install WebSockets {id="install_plugin"}

To install the `WebSockets` plugin, pass it to the `install` function inside
a [client configuration block](create-client.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
//...
val client = HttpClient(CIO) {
    install(WebSockets)
}
```

## Configure WebSockets {id="configure_plugin"}

Optionally, you can configure the plugin inside the `install` block by passing the supported properties of
the [WebSockets.Config](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/-web-sockets/-config/index.html).

* Use the `maxFrameSize` property to set a maximum Frame that could be received or sent.
* Use the `contentConverter` property to set a converter for serialization/deserialization.

```kotlin
```

{src="snippets/client-websockets/src/main/kotlin/com/example/Application.kt" include-lines="12-16"}

* Use the `pingPeriod` property to specify the duration between pings.

The `pingPeriod` property is not applicable for the OkHttp engine.
To set the ping interval for OkHttp, you can use the [engine configuration](http-client_engines.md#okhttp):

```kotlin
import io.ktor.client.engine.okhttp.OkHttp

val client = HttpClient(OkHttp) {
    engine {
        preconfigured = OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .build()
    }
}
```

## Handle a WebSockets session {id="handle-session"}

### API overview {id="api-overview"}

A client's WebSocket session is represented by
the [DefaultClientWebSocketSession](https://api.ktor.io/ktor-shared/ktor-websockets/io.ktor.websocket/-default-web-socket-session/index.html)
interface. This interface exposes the API that allows you to send/receive WebSocket frames and close a
session. `HttpClient` allows you to get access to a WebSocket session in one of the following ways:

- The [webSocket()](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/web-socket.html)
  function accepts `DefaultClientWebSocketSession` as a block argument.
  ```kotlin
     runBlocking {
         client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/echo") {
             // this: DefaultClientWebSocketSession
         }
     }
  ```
-
The [webSocketSession()](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.websocket/web-socket-session.html)
function returns the `DefaultClientWebSocketSession` instance and allows you to access a session outside
the `runBlocking` or `launch` scope.

Within the function block, you define the handler for the specified path. The following functions and properties are
available within the block:

* Use the `send()` function to send text content to the server.
* Use the `incoming` and `outgoing` properties to access the channels for receiving and sending WebSocket frames. A
  frame is represented by the `Frame` class.
* Use the `close()` function to send a close frame with the specified reason.

When handling a session, you can check a frame type, for example:

* `Frame.Text` is a text frame. For this frame type, you can read its content using `Frame.Text.readText()`.
* `Frame.Binary` is a binary frame. For this type, you can read its content using `Frame.Binary.readBytes()`.
* `Frame.Close` is a closing frame. You can call `Frame.Close.readReason()` to get a close reason for the current
  session.

### Example {id="example"}

The example below creates the `echo` WebSocket endpoint and shows how to send and receive a message to and from a
server.

```kotlin
```

{src="snippets/client-websockets/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

For the full example,
see [client-websockets](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-websockets).

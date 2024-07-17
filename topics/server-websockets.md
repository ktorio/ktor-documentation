[//]: # (title: WebSockets)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="WebSockets"/>
<var name="package_name" value="io.ktor.server.websocket"/>
<var name="artifact_name" value="ktor-server-websockets"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="server-websockets"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
The Websockets plugin allows you to create a multi-way communication session between a server and a client.
</link-summary>

<snippet id="websockets-description">

WebSocket is a protocol which provides a full-duplex communication session between the user's browser and a server over
a single TCP connection. It is particularly useful for creating applications that require real-time data transfer from
and to the server.

Ktor supports the WebSocket protocol both on the server-, and the client-side.

</snippet>

Ktor allows you to:

* Configure basic WebSocket settings, such as frame size, a ping period, and so on.
* Handle a WebSocket session for exchanging messages between the server and client.
* Add WebSocket extensions. For example, you can use the [Deflate](server-websocket-deflate.md) extension or
  implement a [custom extension](server-websocket-extensions.md).

> To learn about WebSocket support on the client-side, see [](client-websockets.md).

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install WebSockets {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure WebSockets {id="configure"}

Optionally, you can configure the plugin inside the `install` block by
passing [WebSocketOptions](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-websockets/io.ktor.server.websocket/-web-sockets/-web-socket-options/index.html):

* Use the `pingPeriod` property to specify the duration between pings.
* Use the `timeout` property to set a timeout after which the connection to be closed.
* Use the `maxFrameSize` property to set a maximum Frame that could be received or sent.
* Use the `masking` property to specify whether masking is enabled.
* Use the `contentConverter` property to set a converter for serialization/deserialization.

```kotlin
```

{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" include-lines="13-18"}

## Handle WebSockets sessions {id="handle-sessions"}

### API overview {id="api-overview"}

Once you have installed and configured the `WebSockets` plugin, you can define an endpoint to handle a Websocket
session. To define a WebSocket endpoint on a server, call the `webSocket` function inside
the [routing](server-routing.md#define_route) block:

```kotlin
routing { 
    webSocket("/echo") {
       // Handle a WebSocket session
    }
}
```

In this example, the server accepts WebSocket requests to `ws://localhost:8080/echo` when
a [default configuration](server-configuration-file.topic) is used.

Inside the `webSocket` block, you define the handler for the WebSocket session, which is represented by
the [DefaultWebSocketServerSession](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-websockets/io.ktor.server.websocket/-default-web-socket-server-session/index.html)
class.
The following functions and properties are available within the block:

* Use the `send` function to send text content to the client.
* Use the `incoming` and `outgoing` properties to access the channels for receiving and sending WebSocket frames. A
  frame is represented by the `Frame` class.
* Use the `close` function to send a close frame with the specified reason.

When handling a session, you can check a frame type, for example:

* `Frame.Text` is a text frame. For this frame type, you can read its content using `Frame.Text.readText()`.
* `Frame.Binary` is a binary frame. For this type, you can read its content using `Frame.Binary.readBytes()`.

> Note that the `incoming` channel doesn't contain control frames such as the ping/pong or close frames.
> To handle control frames and reassemble fragmented frames, use
the [webSocketRaw](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-websockets/io.ktor.server.websocket/web-socket-raw.html)
function to handle a WebSocket session.
>
{style="note"}

> To get information about the client (such as the client's IP address), use the `call` property. Learn
about [](server-requests.md#request_information).

Below, we'll take a look at the examples of using this API.

### Example: Handle a single session {id="handle-single-session"}

The example below shows how to create the `echo` WebSocket endpoint to handle a session with one client:

```kotlin
```

{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" include-lines="19,24-36"}

For the full example,
see [server-websockets](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/server-websockets).

### Example: Handle multiple sessions {id="handle-multiple-session"}

To efficiently manage multiple WebSocket sessions and handle broadcasting, you can utilize Kotlin's
[`SharedFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/).
This approach provides a scalable and concurrency-friendly method for managing WebSocket communications. Here's how to
implement this pattern:

1. Define a `SharedFlow` for broadcasting messages:

```kotlin
```

{src="snippets/server-websockets-sharedflow/src/main/kotlin/com/example/plugins/Sockets.kt" include-lines="23-24"}

2. In your WebSocket route, implement the broadcasting and message handling logic:

```kotlin
```

{src="snippets/server-websockets-sharedflow/src/main/kotlin/com/example/plugins/Sockets.kt" include-lines="25-48"}

The `runCatching` block processes
incoming messages and emits them to the `SharedFlow`, which then broadcasts to all collectors.

By using this pattern, you can efficiently manage multiple WebSocket sessions without manually tracking individual
connections. This approach scales well for applications with many concurrent WebSocket connections and provides a clean,
reactive way to handle message broadcasting.

For the full example,
see [server-websockets-sharedflow](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/server-websockets-sharedflow).

## The WebSocket API and Ktor {id="websocket-api"}

The [standard events from the WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) map to
Ktor in the following way:

* `onConnect` happens at the start of the block.
* `onMessage` happens after successfully reading a message (for example, with `incoming.receive()`) or using suspended
  iteration with `for(frame in incoming)`.
* `onClose` happens when the `incoming` channel is closed. That would complete the suspended iteration, or throw
  a `ClosedReceiveChannelException` when trying to receive a message.
* `onError` is equivalent to other exceptions.

In both `onClose` and `onError`, the `closeReason` property is set.

In the following example, the infinite loop will only be exited when an exception has risen (either
a `ClosedReceiveChannelException` or another exception):

```kotlin
webSocket("/echo") {
    println("onConnect")
    try {
        for (frame in incoming){
            val text = (frame as Frame.Text).readText()
            println("onMessage")
            received += text
            outgoing.send(Frame.Text(text))
        }
    } catch (e: ClosedReceiveChannelException) {
        println("onClose ${closeReason.await()}")
    } catch (e: Throwable) {
        println("onError ${closeReason.await()}")
        e.printStackTrace()
    }
}
```


[//]: # (title: Server WebSockets)

<var name="plugin_name" value="WebSockets"/>
<var name="artifact_name" value="ktor-server-websockets"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="server-websockets"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
Ktor supports the WebSocket protocol and allows you to create applications that require real-time data transfer from and to the server.
</excerpt>

Ktor supports the WebSocket protocol and allows you to create applications that require real-time data transfer from and to the server. For example, WebSockets can be used to create a [chat application](creating_web_socket_chat.md).

Ktor allows you to:
* Configure basic WebSocket settings, such as frame size, a ping period, and so on.
* Handle a WebSocket session for exchanging messages between the server and client.
* Add WebSocket extensions. For example, you can use the [Deflate](websocket_deflate_extension.md) extension or implement a [custom extension](websocket_extensions_api.md).

> To learn about WebSocket support in a Ktor client, see [](websocket_client.md).


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install WebSockets {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure WebSockets settings {id="configure"}

Optionally, you can configure the plugin by passing [WebSocketOptions](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-web-sockets/-web-socket-options/index.html) to the `install` function:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="15-20"}

## Handle WebSockets sessions {id="handle-sessions"}

### API overview {id="api-overview"}

After you installed and configured the `WebSockets` plugin, you are ready to handle a WebSocket session. First, you need to define a WebSocket endpoint on a server by calling the [webSocket](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/web-socket.html) function inside the [routing](Routing_in_Ktor.md#define_route) block:
```kotlin
routing { 
    webSocket("/echo") {
       // Handle a WebSocket session
    }
}
```
For such an endpoint, a server accepts WebSocket requests to `ws://localhost:8080/echo` when a [default configuration](Configurations.xml) is used.

Inside the `webSocket` block, you need to handle a WebSocket session, which is represented by the [DefaultWebSocketServerSession](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-default-web-socket-server-session/index.html) class. Session configuration might look as follows:

1. Use the `send` function to send text content to the client. 
2. Use the `incoming` and `outgoing` properties to access the channels for receiving and sending WebSocket frames. A frame is represented by the [Frame](https://api.ktor.io/ktor-http/ktor-http-cio/ktor-http-cio/io.ktor.http.cio.websocket/-frame/index.html) class.
3. When handing a session, you can check a frame type, for example:
   * `Frame.Text` is a text frame. For this frame type, you can read its content using `Frame.Text.readText()`.
   * `Frame.Binary` is a binary frame. For this type, you can read its content using `Frame.Binary.readBytes()`.
   * `Frame.Close` is a closing frame. You can call `Frame.Close.readReason()` to get a close reason for the current session.
4. Use the `close` function to send a close frame with the specified reason.

> If you need to get information about the client (for example, the client IP address), use the [call](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-web-socket-server-session/call.html) property. You can learn more from [](requests.md#request_information).

Below we'll take a look at the examples of using this API.


### Example: Handle a single session {id="handle-single-session"}

The example below shows how to create the `echo` WebSocket endpoint to handle a session with one client:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="21,26-40,60-61"}

You can find the full example here: [server-websockets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets).

### Example: Handle multiple sessions {id="handle-multiple-session"}

To handle multiple WebSocket sessions (for example, for a chat application), you need to store each session on a server. For example, you can define a connection with a unique name and associate it with a specified session. A sample `Connection` class below shows how to do this:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="63-69"}

Then, you can create a new connection inside the `webSocket` handler when a new client connects to the WebSocket endpoint:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="21,42-61"}

You can find the full example here: [server-websockets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets).



## The WebSocket API and Ktor {id="websocket-api"}

The [standard events from the WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) map to Ktor in the following way:

* `onConnect` happens at the start of the block.
* `onMessage` happens after successfully reading a message (for example, with `incoming.receive()`) or using suspended iteration with `for(frame in incoming)`.
* `onClose` happens when the `incoming` channel is closed. That would complete the suspended iteration, or throw a `ClosedReceiveChannelException` when trying to receive a message`.
* `onError` is equivalent to other exceptions.

In both `onClose` and `onError`, the [closeReason](https://api.ktor.io/ktor-http/ktor-http-cio/ktor-http-cio/io.ktor.http.cio.websocket/-default-web-socket-session/close-reason.html) property is set.

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

In this sample, the infinite loop is only exited with an exception is risen: either a `ClosedReceiveChannelException` or another exception.

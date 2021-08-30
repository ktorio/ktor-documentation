[//]: # (title: WebSockets)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-websockets</code>
</p>
<var name="example_name" value="server-websockets"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor supports the WebSocket protocol and allows you to create applications that require real-time data transfer from and to the server. For example, you can create a [chat application](creating_web_socket_chat.md), which uses WebSockets.

Ktor allows you to:
* Configure basic WebSocket settings, such as frame size, a ping period, and so on.
* Configure a WebSocket endpoint and specify logic for exchanging frames.
* Add WebSocket extensions. For example, you can use the [Deflate](websocket_deflate_extension.md) extension or implement a [custom extension](websocket_extensions_api.md).


## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="WebSockets"/>
<var name="artifact_name" value="ktor-websockets"/>
<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install WebSockets {id="install_feature"}

<var name="feature_name" value="WebSockets"/>
<include src="lib.xml" include-id="install_feature"/>


## Configure WebSockets settings {id="configure"}

Optionally, you can configure the plugin by passing [WebSocketOptions](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-web-sockets/-web-socket-options/index.html) to the `install` function:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="15-20"}

## Handle WebSockets sessions {id="handle-sessions"}

After you installed and configured the `WebSockets` plugin, you are ready to handle a WebSocket session. First, you need to define a WebSocket endpoint on a server by calling the [webSocket](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/web-socket.html) function inside the [routing](Routing_in_Ktor.md#define_route) block:
```kotlin
routing { 
    webSocket("/echo") {
       // Handle a WebSocket session
    }
}
```
For such an endpoint, a server accepts WebSocket requests to `ws://localhost:8080/echo` in a [default configuration](Configurations.xml).

Inside the `webSocket` block, you need to handle a WebSocket session, which is represented by the [DefaultWebSocketServerSession](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-default-web-socket-server-session/index.html) class. Session configuration might look as follows:

1. Use the `send` function to send text content to the client. 
2. Use the `incoming` and `outgoing` properties to access the channels for receiving and sending WebSocket frames ([Frame](https://api.ktor.io/ktor-http/ktor-http-cio/ktor-http-cio/io.ktor.http.cio.websocket/-frame/index.html)). 
3. When handing a session, you can check a frame type, for example:
   * `Frame.Text` is a text frame. For this frame type, you can read its content using `Frame.Text.readText()`.
   * `Frame.Binary` is a binary frame. For this type, you can read its content using `Frame.Binary.readBytes()`.
   * `Frame.Close` is a closing frame. You can call `Frame.Close.readReason()` to get a close reason for the current session.
4. Use the `close` function to send a close frame with the specified reason.

> If you need to get information about the client (for example, the client IP address), use the [call](https://api.ktor.io/ktor-features/ktor-websockets/ktor-websockets/io.ktor.websocket/-web-socket-server-session/call.html) property. You can learn more from [](requests.md#request_information).

Below we'll take a look at the examples of using this API.


### Handle a single session {id="handle-single-session"}

The example below shows how to create the `echo` WebSocket endpoint to handle a session with one client:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="21,26-40,60-61"}

You can find the full example here: [server-websockets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets)

### Handle multiple sessions {id="handle-multiple-session"}

To handle multiple WebSocket sessions (for example, for a chat application), you need to store each session on a server. For example, you can define a connection with a unique name and associate it with a specified session. A sample `Connection` class below shows how to do this:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="63-69"}

Then, you can create a new connection inside the `webSocket` handler when a new client connects to the WebSocket endpoint:

```kotlin
```
{src="snippets/server-websockets/src/main/kotlin/com/example/Application.kt" lines="21,42-61"}

You can find the full example here: [server-websockets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets)




## Testing {id="testing"}

You can [test](Testing.md) WebSocket conversations by using the `handleWebSocketConversation` function inside the `withTestApplication` block:

```kotlin
```
{src="snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt" include-symbol="ModuleTest"}

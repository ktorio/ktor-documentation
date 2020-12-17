[//]: # (title: Server WebSockets)

<include src="lib.md" include-id="outdated_warning"/>

This feature adds WebSockets support to Ktor.
WebSockets are a mechanism to keep a bi-directional real-time ordered connection between
the server and the client.
Each message from this channel is called Frame: a frame can be a text or binary message,
or a close or ping/pong message. Frames can be marked as incomplete or final.


## Installing
{id="installing"}

In order to use the WebSockets functionality you first have to install it: 

```kotlin
install(WebSockets)
```

If required, you can adjust parameters during the installation of the feature:

```kotlin
install(WebSockets) {
    pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
    timeout = Duration.ofSeconds(15)
    maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length. 
    masking = false
    
    extensions { 
        // install(...)
    }
}
```

## Usage
{id="usage"}

Once installed, you can define the `webSocket` routes for the [routing](Routing_in_Ktor.md) feature:

Instead of the short-lived normal route handlers, webSocket handlers are meant to be long-lived.
And all the relevant WebSocket methods are suspended so that the function will be suspended in
a non-blocking way while receiving or sending messages.

`webSocket` methods receive a callback with a [WebSocketSession](#WebSocketSession)
instance as the receiver. That interface defines an `incoming` (ReceiveChannel) property and an `outgoing` (SendChannel)
property, as well as a `close` method. Check the full [WebSocketSession](#WebSocketSession) for more information.

### Usage as an suspend actor {id="actor"}

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}
```

>An exception will be thrown while receiving a Frame if the client closes the connection
>explicitly or the TCP socket is closed. So even with a `while (true)` loop, this shouldn't be
>a leak.
>
{type="note"}

### Usage as a Channel {id="channel"}

Since the `incoming` property is a ReceiveChannel, you can use it with its stream-like interface:

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming.mapNotNull { it as? Frame.Text }) {
            val text = frame.readText()
            outgoing.send(Frame.Text("YOU SAID $text"))
            if (text.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            }
        }
    }
}
``` 

## Interface {id="interface"}

### The WebSocketSession interface {id="WebSocketSession"}

You receive a WebSocketSession as the receiver (this), giving you direct access
to these members inside your webSocket handler.

```kotlin
interface WebSocketSession {
    // Basic interface
    val incoming: ReceiveChannel<Frame> // Incoming frames channel
    val outgoing: SendChannel<Frame> // Outgoing frames channel
    fun close(reason: CloseReason)

    // Convenience method equivalent to `outgoing.send(frame)`
    suspend fun send(frame: Frame) // Enqueue frame, may suspend if the outgoing queue is full. May throw an exception if the outgoing channel is already closed, so it is impossible to transfer any message.

    // The call and the context
    val call: ApplicationCall
    val application: Application
    
    // List of WebSocket extensions negotiated for the current session
    val extensions: List<WebSocketExtension<*>>

    // Modifiable properties for this request. Their initial value comes from the feature configuration.
    var pingInterval: Duration?
    var timeout: Duration
    var masking: Boolean // Enable or disable masking output messages by a random xor mask.
    var maxFrameSize: Long // Specifies frame size limit. The connection will be closed if violated
    
    // Advanced
    val closeReason: Deferred<CloseReason?>
    suspend fun flush() // Flush all outstanding messages and suspend until all earlier sent messages will be written. Could be called at any time even after close. May return immediately if connection is already terminated.
    fun terminate() // Initiate connection termination immediately. Termination may complete asynchronously.
}
```

>If you need information about the connection. For example the client ip, you have access
>to the call property. So you can do things like `call.request.origin.host` inside
>your websocket block.
>
{type="note"}

### The Frame interface
{id="Frame"}

A frame is each packet sent and received at the WebSocket protocol level.
There are two message types: TEXT and BINARY. And three control packets: CLOSE, PING, and PONG.
Each packet has a payload `buffer`. And for Text or Close messages, you can
call the `readText` or `readReason` to interpret that buffer.

```kotlin
enum class FrameType { TEXT, BINARY, CLOSE, PING, PONG }
```

```kotlin
sealed class Frame {
    val fin: Boolean // Is this frame a final frame?
    val frameType: FrameType // The Type of the frame
    val buffer: ByteBuffer // Payload
    val disposableHandle: DisposableHandle
    
    // Extension bits
    val rsv1: Boolean
    val rsv2: Boolean
    val rsv3: Boolean

    class Binary : Frame
    class Text : Frame {
        fun readText(): String
    }
    class Close : Frame {
        fun readReason(): CloseReason?
    }
    class Ping : Frame
    class Pong : Frame
}
```

## Testing
{id="testing"}

You can test WebSocket conversations by using the `handleWebSocketConversation`
method inside a `withTestApplication` block.


```kotlin
class MyAppTest {
    @Test
    fun testConversation() {
        withTestApplication {
            application.install(WebSockets)
    
            val received = arrayListOf<String>()
            application.routing {
                webSocket("/echo") {
                    try {
                        while (true) {
                            val text = (incoming.receive() as Frame.Text).readText()
                            received += text
                            outgoing.send(Frame.Text(text))
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        // Do nothing!
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
    
            handleWebSocketConversation("/echo") { incoming, outgoing ->
                val textMessages = listOf("HELLO", "WORLD")
                for (msg in textMessages) {
                    outgoing.send(Frame.Text(msg))
                    assertEquals(msg, (incoming.receive() as Frame.Text).readText())
                }
                assertEquals(textMessages, received)
            }
        }
    }
}
```

## FAQ

### Standard Events: `onConnect`, `onMessage`, `onClose` and `onError`
{id="standard-events"}

How do the [standard events from the WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) maps to Ktor?

* `onConnect` happens at the start of the block.
* `onMessage` happens after successfully reading a message (for example with `incoming.receive()`) or using suspended iteration with `for(frame in incoming)`.
* `onClose` happens when the `incoming` channel is closed. That would complete the suspended iteration, or throw a `ClosedReceiveChannelException` when trying to receive a message`.
* `onError` is equivalent to other other exceptions.

In both `onClose` and `onError`, the [`closeReason` property](https://api.ktor.io/1.0.0-beta-1/io.ktor.http.cio.websocket/-default-web-socket-session/close-reason.html) is set.

To illustrate this:

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
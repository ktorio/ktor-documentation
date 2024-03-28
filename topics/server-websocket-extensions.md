[//]: # (title: WebSocket extensions API)

Ktor WebSocket API supports writing your own extensions(such as [RFC-7692](https://tools.ietf.org/html/rfc7692))
or any custom extensions.

## Install extension

To install and configure the extensions, we provide two methods: `extensions` and `install` which can be used in the following way:
```kotlin
install(WebSockets) {
    extensions { /* WebSocketExtensionConfig.() -> Unit */
        install(MyWebSocketExtension) { /* MyWebSocketExtensionConfig.() -> Unit */
        /* Optional extension configuration. */ 
        }
    }
}
```

The extensions are used in order of installation.

## Check if the extension is negotiated

All installed extensions go through the negotiation process, and those that are successfully negotiated are used during the request.
You can use `WebSocketSession.extensions: : List<WebSocketExtension<*>>` property with a list of all extensions used
by for the current session.

There are two methods to check if the extension is in use: `WebSocketSession.extension` and `WebSocketSession.extensionOrNull`:
```kotlin
webSocket("/echo") {
    val myExtension = extension(MyWebSocketExtension) // will throw if `MyWebSocketExtension` is not negotiated
    // or
    val myExtension = extensionOrNull(MyWebSocketExtension) ?: close() // will close the session if `MyWebSocketExtension` is not negotiated
}
```

## Write a new extension

There are two interfaces for implementing a new extension: `WebSocketExtension<ConfigType: Any>` and
`WebSocketExtensionFactory<ConfigType : Any, ExtensionType : WebSocketExtension<ConfigType>>`.
A single implementation can work for both clients and servers.

Below is an example of how a simple frame logging extension can be implemented:

```kotlin
class FrameLoggerExtension(val logger: Logger) : WebSocketExtension<FrameLogger.Config> {
```

The plugin has two groups of fields and methods. The first group is for extension negotiation:

```kotlin
    /** A list of protocols to be sent in a client request for negotiation **/
    override val protocols: List<WebSocketExtensionHeader> = emptyList()
   
    /** 
      * This method will be called for server and will process `requestedProtocols` from the client.
      * As a result, it will return a list of extensions that server agrees to use.
      */
    override fun serverNegotiation(requestedProtocols: List<WebSocketExtensionHeader>): List<WebSocketExtensionHeader> {
        logger.log("Server negotiation")
        return emptyList()
    }

    /**
      * This method will be called on the client with a list of protocols, produced by `serverNegotiation`. It will decide if these extensions should be used. 
      */ 
    override fun clientNegotiation(negotiatedProtocols: List<WebSocketExtensionHeader>): Boolean {
        logger.log("Client negotiation")
        return true
    }

```

The second group is the place for actual frame processing. Methods will take a frame and produce a new processed frame if necessary:

```kotlin
    override fun processOutgoingFrame(frame: Frame): Frame {
        logger.log("Process outgoing frame: $frame")
        return frame
    }

    override fun processIncomingFrame(frame: Frame): Frame {
        logger.log("Process incoming frame: $frame")
        return frame
    }
```

There are also some implementation details: the plugin has `Config` and reference to the origin `factory`.

```kotlin
    class Config {
        lateinit var logger: Logger
    }

    /**
    * A factory which can create a current extension instance. 
    */
    override val factory: WebSocketExtensionFactory<Config, FrameLogger> = FrameLoggerExtension
```

The factory is usually implemented in a companion object (similar to regular plugins):

```kotlin
    companion object : WebSocketExtensionFactory<Config, FrameLogger> {
        /* Key to discover installed extension instance */
        override val key: AttributeKey<FrameLogger> = AttributeKey("frame-logger")

        /** List of occupied rsv bits.
         * If the extension occupies a bit, it can't be used in other installed extensions. We use these bits to prevent plugin conflicts(prevent installing multiple compression plugins). If you're implementing a plugin using some RFC, rsv occupied bits should be referenced there.
         */
        override val rsv1: Boolean = false
        override val rsv2: Boolean = false
        override val rsv3: Boolean = false

       /** Create plugin instance. Will be called for each WebSocket session **/
        override fun install(config: Config.() -> Unit): FrameLogger {
            return FrameLogger(Config().apply(config).logger)
        }
    }
}
```

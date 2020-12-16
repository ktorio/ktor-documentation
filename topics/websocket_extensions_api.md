[//]: # (title: WebSocket extensions API)

Ktor WebSocket API supports writing your own extensions(like [RFC-7692](https://tools.ietf.org/html/rfc7692))
or any custom extensions.

This API is production ready, but can be slightly modified in the minor release. To indicate this we require users to
explicitly optin `@ExperimentalWebSocketExtensionsApi` annotation:
```kotlin
@OptIn(ExperimentalWebSocketExtensionApi::class)
```
If you want to leave your feedback or subscribe on updates, check
[KTOR-688](https://youtrack.jetbrains.com/issue/KTOR-688) design issue.

## Installing extension

To install and configure the extensions we provide 2 methods:  `extensions` and `install` which can be used in the following way:
```kotlin
install(WebSockets) {
    extensions { /* WebSocketExtensionConfig.() -> Unit */
        install(MyWebSocketExtension) { /* MyWebSocketExtensionConfig.() -> Unit */
        /* Optional extension configuration. */ 
        }
    }
}
```

The extensions are used in order of installation. This way looks familiar to existing features installation API and works for client and server in the same way.


## Checking if the extension is negotiated

All installed extensions go through negotiation process and only negotiated extensions are used for the request.
We introduce `WebSocketSession.extensions: : List<WebSocketExtension<*>>` property with list of all extensions used by for the current session.

There are 2 methods to check if the extension is in use: `WebSocketSession.extension` and `WebSocketSession.extensionOrNull`:
```kotlin
webSocket("/echo") {
    val myExtension = extension(MyWebSocketException) // will throw if `MyWebSocketException` is not negotiated
    // or
    val myExtension = extensionOrNull(MyWebSocketException) ?: close() // will close the session if `MyWebSocketException` is not negotiated
}
```

## Writing new extension


There are 2 interfaces for implementing new extension: `WebSocketExtension<ConfigType: Any>` and `WebSocketExtensionFactory<ConfigType : Any, ExtensionType : WebSocketExtension<ConfigType>>`. The new extension can be made by implementing all of them. The single implementation is working for client and server as well.

Here is the example of how the simple frame logging extension can be implemented:

```kotlin
class FrameLoggerExtension(val logger: Logger) : WebSocketExtension<FrameLogger.Config> {
```

The feature has 2 groups of fields and methods. The first group is for extension negotiation:
```kotlin
    /** List of protocols will be send in client request for negotiation **/
    override val protocols: List<WebSocketExtensionHeader> = emptyList()
   
    /** 
      * This method will be called for server and will process `requestedProtocols` from client.
      * In the result it will return list of extensions that server agrees to use.
      */
    override fun serverNegotiation(requestedProtocols: List<WebSocketExtensionHeader>): List<WebSocketExtensionHeader> {
        logger.log("Server negotiation")
        return emptyList()
    }

    /**
      * This method will be called on the client with list of protocols, produced by `serverNegotiation`. It will decide if this extensions should be used. 
      */ 
    override fun clientNegotiation(negotiatedProtocols: List<WebSocketExtensionHeader>): Boolean {
        logger.log("Client negotiation")
        return true
    }

```

The second group is the place for actual frame processing. Metods will take frame and produce new processed frame if necessarry:
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

There are also some implementation details: the feature has `Config` and referenfe to the origin `factory.
```kotlin
    class Config {
        lateinit var logger: Logger
    }

    /**
    * Factory which can create current extension instance. 
    */
    override val factory: WebSocketExtensionFactory<Config, FrameLogger> = FrameLoggerExtension
```
The factory is usually implemented in companion object(similar to regular features):
```kotlin
    companion object : WebSocketExtensionFactory<Config, FrameLogger> {
        /* Key to discover installed extension instance */
        override val key: AttributeKey<FrameLogger> = AttributeKey("frame-logger")

        /** List of occupied rsv bits.
         * If the extension occupy a bit, it can't be used in other installed extensions. We use that bits to prevent feature conflicts(prevent to install multiple compression features). If you implementing feature using some RFC, rsv occupied bits should be referenced there.
         */
        override val rsv1: Boolean = false
        override val rsv2: Boolean = false
        override val rsv3: Boolean = false

       /** Create feture instance. Will be called for each WebSocket session **/
        override fun install(config: Config.() -> Unit): FrameLogger {
            return FrameLogger(Config().apply(config).logger)
        }
    }
}
```

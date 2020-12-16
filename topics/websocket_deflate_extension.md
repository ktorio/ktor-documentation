[//]: # (title: WebSocket Deflate extension)

Ktor implements `Deflate` WebSocket extensions [RFC-7692](https://tools.ietf.org/html/rfc7692) 
for client and server. The extension can transparently compress frames before sending and decompress after receive. 
It's usefull to enable this extension if you're sending large amount of text data (or any kind of data that can be 
deflated efficiently).


This API is production ready, but can be slightly modified in the minor release. To indicate this we require users to
explicitly optin `@ExperimentalWebSocketExtensionsApi` annotation:
```kotlin
@OptIn(ExperimentalWebSocketExtensionApi::class)
```
If you want to leave your feedback or subscribe on updates, check 
[KTOR-688](https://youtrack.jetbrains.com/issue/KTOR-688) design issue.


## Installation

To use the extension it should be installed first. To do that we can use `install` method in `extensions` block:

```kotlin
// For client and server
install(WebSockets) {
    extensions {
        install(WebSocketDeflateExtension) {
            /**
             * Compression level to use for [java.util.zip.Deflater].
             */
            compressionLevel = Deflater.DEFAULT_COMPRESSION

            /**
             * Prevent to compress small outgoing frames.
             */
            compressIfBiggerThan(bytes = 4 * 1024)
        }
    }
}
```

There are also several parameters that can be configured:

### Advanced configuration parameters 

#### Context takeover

Specify if the client(server) should use compression window. Enabling these parameters reduce the amount of space 
allocated per single session. Please note that window size can not be configured due to limitations 
of [java.util.zip.Deflater] API and always equal to `15`.
```kotlin
clientNoContextTakeOver = false

serverNoContextTakeOver = false
```

These parameters are described in [RFC-7692 Section 7.1.1](https://tools.ietf.org/html/rfc7692#section-7.1.1)

#### Specify compress condition

To specify compress condition explicitly `compressIf` method can be used. To compress text-only frames we can specify:
```kotlin
compressIf { frame -> 
    frame is Frame.Text
}
```
All calls to `compressIf` will be evaluated before compress a frame.

#### Fine-tune list of protocols

The list of protocols to send can be edited as desired using `configureProtocols` method:

```kotlin
configureProtocols { protocols ->
    protocols.clear()
    protocols.add(...)
}
```

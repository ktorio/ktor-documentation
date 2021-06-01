[//]: # (title: WebSocket Deflate extension)

Ktor implements `Deflate` WebSocket extensions [RFC-7692](https://tools.ietf.org/html/rfc7692) 
for the client and server. The extension can transparently compress frame before sending, and decompress after receiving.
It's useful to enable this extension if you're sending large amounts of text data.

<var name="annotation_name" value="ExperimentalWebSocketExtensionsApi"/>
<var name="issue_number" value="688"/>
<include src="lib.xml" include-id="experimental"/>

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

### Advanced configuration parameters 

#### Context takeover

Specify if the client (and server) should use compression window. Enabling these parameters reduce the amount of space 
allocated per single session. Please note that window size
cannot be configured due to limitations of `java.util.zip.Deflater` API. The value is fixed to `15`.

```kotlin
clientNoContextTakeOver = false

serverNoContextTakeOver = false
```

These parameters are described in [RFC-7692 Section 7.1.1](https://tools.ietf.org/html/rfc7692#section-7.1.1)

#### Specify compress condition

To specify a compress condition explicitly, you can use the `compressIf` method. For instance, to compress text-only:

```kotlin
compressIf { frame -> 
    frame is Frame.Text
}
```
All calls to `compressIf` will be evaluated before compression takes place.

#### Fine-tune list of protocols

The list of protocols to send can be edited as needed using the `configureProtocols` method:

```kotlin
configureProtocols { protocols ->
    protocols.clear()
    protocols.add(...)
}
```

[//]: # (title: kotlinx-io)
[//]: # (caption: "kotlinx-io)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (children: /kotlinx/io/)
[//]: # (permalink: /kotlinx/io.html)
[//]: # (ktor_version_review: 1.0.0)

The official multiplatform asynchronous I/O for Kotlin. It depends on [kotlinx.coroutines] and [kotlinx.atomicfu].

[kotlinx.atomicfu]: /kotlinx/atomicfu.html
[kotlinx.coroutines]: /kotlinx/coroutines.html

This project exposes common functionality for doing I/O in a way that works in all the targets and uses coroutines
to expose a non-blocking API that can be written as plain linear code easily.

`kotlinx-io` is OpenSource and you can find it at GitHub: <https://github.com/Kotlin/kotlinx-io>

## Encoding:
* [Charset](/kotlinx/io/encoding/charset.html) - Decoding/encoding Strings from/to ByteArrays.
* [ByteOrder](/kotlinx/io/encoding/byteorder.html) - To express which Endian (Little or Big) to use for converting numbers from/to octets.

## I/O:
* [Byte Channels](/kotlinx/io/io/channels.html) - Asynchronous byte streams without seeking support, consumed once.
* [Input/Output](/kotlinx/io/io/input-output.html) - Interfaces with the functionality exposed by IoBuffers and Packets.
* [IoBuffer](/kotlinx/io/io/iobuffer.html) - A seekable View of a fixed buffer/memory chunk similar to Java’s ByteBuffer.
* [Packets](/kotlinx/io/io/packets.html) - Synchronous streams without seeking support, constructed once with a potentially unknown size, consumed once.

## Tools:
* [ObjectPool](/kotlinx/io/tools#objectpool) - Generic, lock-free and concurrent ObjectPool.

## Platforms:
* [JVM](/kotlinx/io/platforms.html#jvm) - Tools for conversions between CIO and NIO Buffers, and Java Streams.
* [JavaScript](/kotlinx/io/platforms.html#js) - Tools for TypedArrays, WebSockets, MessageEvent and XMLHttpRequest.
* [Native](/kotlinx/io/platforms.html#native) - *No special APIs to interact with K/N primitives yet*
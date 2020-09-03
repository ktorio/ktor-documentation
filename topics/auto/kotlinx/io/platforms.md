[//]: # (title: Platforms)
[//]: # (caption: Platforms)
[//]: # (category: kotlinx)
[//]: # (toc: false)
[//]: # (permalink: /kotlinx/io/platforms.html)
[//]: # (ktor_version_review: 1.0.0)

Platform-specific functionality to deal with each platform APIs.

## JVM
{ #jvm }

### NIO Channels

```kotlin

// Packets
fun WritableByteChannel.writePacket(builder: BytePacketBuilder.() -> Unit): ByteReadPacket?
fun WritableByteChannel.writePacket(p: ByteReadPacket): Boolean
fun ReadableByteChannel.readPacketExact(n: Long): ByteReadPacket
fun ReadableByteChannel.readPacketAtLeast(n: Long): ByteReadPacket
fun ReadableByteChannel.readPacketAtMost(n: Long): ByteReadPacket

// IoBuffer
fun ReadableByteChannel.read(buffer: IoBuffer): Int
fun WritableByteChannel.write(buffer: IoBuffer): Int

// Input/Output
fun ReadableByteChannel.asInput(pool: ObjectPool<IoBuffer> = IoBuffer.Pool): Input = ChannelAsInput(this, pool)
fun WritableByteChannel.asOutput(pool: ObjectPool<IoBuffer> = IoBuffer.Pool): Output = ChannelAsOutput(pool, this)
```

### InputStream and OutputStream

```kotlin
// Packets
fun OutputStream.writePacket(builder: BytePacketBuilder.() -> Unit)
fun OutputStream.writePacket(packet: ByteReadPacket)
fun InputStream.readPacketExact(n: Long): ByteReadPacket
fun InputStream.readPacketAtLeast(n: Long): ByteReadPacket
fun InputStream.readPacketAtMost(n: Long): ByteReadPacket
fun ByteReadPacket.inputStream(): InputStream
fun ByteReadPacket.readerUTF8(): Reader
fun BytePacketBuilder.outputStream(): OutputStream
fun BytePacketBuilder.writerUTF8(): Writer

// Input/Output
fun InputStream.asInput(pool: ObjectPool<IoBuffer> = IoBuffer.Pool): Input = InputStreamAsInput(this, pool)
fun OutputStream.asOutput(): Output = OutputStreamAdapter(IoBuffer.Pool, this)
```

## JavaScript
{ #js }

### TypedArrays

```kotlin
fun ByteReadPacket.readArrayBuffer(n: Int = remaining.coerceAtMostMaxInt()): ArrayBuffer
fun BytePacketBuilder.writeFully(src: ArrayBuffer, offset: Int = 0, length: Int = src.byteLength - offset)
fun BytePacketBuilder.writeFully(src: Int8Array, offset: Int = 0, length: Int = src.length - offset)
```

### WebSockets

```kotlin
fun WebSocket.sendPacket(packet: ByteReadPacket)
fun WebSocket.sendPacket(block: BytePacketBuilder.() -> Unit)
```

### MessageEvent

```kotlin
fun MessageEvent.packet(): ByteReadPacket
```

### XMLHttpRequest

```kotlin
fun XMLHttpRequest.sendPacket(block: BytePacketBuilder.() -> Unit)
fun XMLHttpRequest.sendPacket(packet: ByteReadPacket)
fun XMLHttpRequest.responsePacket(): ByteReadPacket = when (responseType)
```

## Native

No special APIs to interact with K/N primitives at this point
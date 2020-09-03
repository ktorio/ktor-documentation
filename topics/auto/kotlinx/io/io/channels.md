[//]: # (title: Byte Channels)
[//]: # (caption: Byte Channels)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

`ByteChannel`s are asynchronous streams, potentially large without seeking capabilities.
They are useful for reading and writing to sockets, or files from the start to the end.
Usually you read and create chunks of synchronous data called [Packets](/kotlinx/io/io/packets.html) from them,
reducing the amount of suspensions to the number of packets read/written.

## ByteReadChannel

```kotlin
interface ByteReadChannel {
    val availableForRead: Int
    val isClosedForRead: Boolean
    val isClosedForWrite: Boolean
    var readByteOrder: ByteOrder

    suspend fun readAvailable(dst: ByteArray, offset: Int, length: Int): Int
    suspend fun readAvailable(dst: IoBuffer): Int
    suspend fun readAvailable(dst: ArrayBuffer, offset: Int, length: Int): Int

    suspend fun readFully(dst: ByteArray, offset: Int, length: Int)
    suspend fun readFully(dst: IoBuffer, n: Int)
    suspend fun readFully(dst: ArrayBuffer, offset: Int, length: Int)

    suspend fun readPacket(size: Int, headerSizeHint: Int): ByteReadPacket
    suspend fun readRemaining(limit: Long, headerSizeHint: Int): ByteReadPacket
    suspend fun readLong(): Long
    suspend fun readInt(): Int
    suspend fun readShort(): Short
    suspend fun readByte(): Byte
    suspend fun readBoolean(): Boolean
    suspend fun readDouble(): Double
    suspend fun readFloat(): Float
    suspend fun readSuspendableSession(consumer: suspend SuspendableReadSession.() -> Unit)
    suspend fun <A : Appendable> readUTF8LineTo(out: A, limit: Int): Boolean
    suspend fun readUTF8Line(limit: Int): String?
    suspend fun discard(max: Long): Long

    fun readSession(consumer: ReadSession.() -> Unit)
    fun cancel(cause: Throwable?): Boolean

    companion object {
        val Empty: ByteReadChannel
    }
}
```

```kotlin
interface ReadSession {
    val availableForRead: Int
    fun discard(n: Int): Int
    fun request(atLeast: Int = 1): IoBuffer?
}

interface SuspendableReadSession : ReadSession {
    suspend fun await(atLeast: Int = 1): Boolean
}
```

## ByteWriteChannel

```kotlin
interface ByteWriteChannel {
    val availableForWrite: Int
    val isClosedForWrite: Boolean
    val autoFlush: Boolean
    var writeByteOrder: ByteOrder
    val closedCause: Throwable?
    suspend fun writeAvailable(src: ByteArray, offset: Int, length: Int): Int
    suspend fun writeAvailable(src: IoBuffer): Int
    suspend fun writeFully(src: ByteArray, offset: Int, length: Int)
    suspend fun writeFully(src: IoBuffer)
    suspend fun writeSuspendSession(visitor: suspend WriterSuspendSession.() -> Unit)
    suspend fun writePacket(packet: ByteReadPacket)
    suspend fun writeLong(l: Long)
    suspend fun writeInt(i: Int)
    suspend fun writeShort(s: Short)
    suspend fun writeByte(b: Byte)
    suspend fun writeDouble(d: Double)
    suspend fun writeFloat(f: Float)
    fun close(cause: Throwable?): Boolean
    fun flush()
}
```
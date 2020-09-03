[//]: # (title: Input/Output)
[//]: # (caption: Input/Output)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

[Input](#input) and [Output](#output) interfaces are synchronous interfaces for reading and writing binary data.
They are implemented by [IOBuffer](/kotlinx/io/io/iobuffer.html), [BytePacketBuilder](/kotlinx/io/io/packets.html#building-packets) and [ByteReadPacket](/kotlinx/io/io/packets.html#reading-packets).

## Input 

```kotlin
// To make this less verbose:
//   NumArray = ByteArray | ShortArray | CharArray | IntArray | LongArray | FloatArray | DoubleArray 

interface Input : Closeable {
    var byteOrder: ByteOrder
    val endOfInput: Boolean

    fun readByte(): Byte
    fun readShort(): Short
    fun readInt(): Int
    fun readLong(): Long
    fun readFloat(): Float
    fun readDouble(): Double

    fun readFully(dst: NumArray, offset: Int, length: Int)
    fun readFully(dst: IoBuffer, length: Int = dst.writeRemaining)

    fun readAvailable(dst: NumArray, offset: Int, length: Int): Int
    fun readAvailable(dst: IoBuffer, length: Int): Int

    fun tryPeek(): Int
    fun discard(n: Long): Long

    fun close()
}

fun Input.readFully(dst: NumArray, offset: Int = 0, length: Int = dst.size)
fun Input.readFully(dst: IoBuffer, length: Int = dst.writeRemaining)
fun Input.readAvailable(dst: NumArray, offset: Int = 0, length: Int = dst.size): Int
fun Input.readAvailable(dst: IoBuffer, length: Int = dst.writeRemaining): Int
fun Input.discard(): Long
fun Input.discardExact(n: Long)
fun Input.discardExact(n: Int)

inline fun Input.takeWhile(block: (IoBuffer) -> Boolean)
inline fun Input.takeWhileSize(initialSize: Int = 1, block: (IoBuffer) -> Int)
```

## Output

```kotlin
// To make this less verbose:
//   NumArray = ByteArray | ShortArray | CharArray | IntArray | LongArray | FloatArray | DoubleArray 

interface Output : Appendable, Closeable {
    var byteOrder: ByteOrder

    fun writeByte(v: Byte)
    fun writeShort(v: Short)
    fun writeInt(v: Int)
    fun writeLong(v: Long)
    fun writeFloat(v: Float)
    fun writeDouble(v: Double)

    fun writeFully(src: NumArray, offset: Int, length: Int)
    fun writeFully(src: IoBuffer, length: Int)

    fun append(csq: CharArray, start: Int, end: Int): Appendable

    fun fill(n: Long, v: Byte)

    fun flush()
    fun close()
}

fun Output.append(csq: CharSequence, start: Int = 0, end: Int = csq.length): Appendable
fun Output.append(csq: CharArray, start: Int = 0, end: Int = csq.size): Appendable
fun Output.writeFully(src: NumArray, offset: Int = 0, length: Int = src.size)
fun Output.writeFully(src: IoBuffer, length: Int = src.readRemaining)
fun Output.fill(n: Long, v: Byte = 0)
inline fun Output.writeWhile(block: (IoBuffer) -> Boolean)
inline fun Output.writeWhileSize(initialSize: Int = 1, block: (IoBuffer) -> Int)
fun Output.writePacket(packet: ByteReadPacket)
```

### Closeable

```kotlin
interface Closeable {
    fun close()
}

inline fun <C : Closeable, R> C.use(block: (C) -> R): R
```
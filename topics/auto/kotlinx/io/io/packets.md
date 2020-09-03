[//]: # (title: Packets)
[//]: # (caption: Packets)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

Packets are small chunks of data representing messages, headers or chunks of information.
They are built and consumed synchronously. And they implement the [Input/Output](/kotlinx/io/io/input-output.html) interfaces.

Unlike [`IOBuffer`](/kotlinx/io/io/iobuffer.html), you can build packets without having to know their size beforehand. 

## Building packets

```kotlin
// To make this less verbose:
//   NumArray = ByteArray | ShortArray | CharArray | IntArray | LongArray | FloatArray | DoubleArray 

fun buildPacket(headerSizeHint: Int = 0, block: BytePacketBuilder.() -> Unit): ByteReadPacket
fun BytePacketBuilder(headerSizeHint: Int = 0): BytePacketBuilder

class BytePacketBuilder(headerSizeHint: Int, pool: ObjectPool<IoBuffer>) : Appendable, Output {
    val size: Int
    val isEmpty: Boolean
    val isNotEmpty: Boolean

    override fun append(c: Char): BytePacketBuilder
    override fun append(csq: CharSequence?): BytePacketBuilder
    override fun append(csq: CharSequence?, start: Int, end: Int): BytePacketBuilder
    override fun release()
    override fun flush()
    override fun close()
    fun <R> preview(block: (tmp: ByteReadPacket) -> R): R
    fun build(): ByteReadPacket
    override fun writePacket(p: ByteReadPacket)
    override fun last(buffer: IoBuffer)
    final override var byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN
    internal var tail: IoBuffer = IoBuffer.Empty
    final override fun writeFully(src: ByteArray, offset: Int, length: Int)
    final override fun writeLong(v: Long)
    final override fun writeInt(v: Int)
    final override fun writeShort(v: Short)
    final override fun writeByte(v: Byte)
    final override fun writeDouble(v: Double)
    final override fun writeFloat(v: Float)
    override fun writeFully(src: NumArray, offset: Int, length: Int)
    override fun writeFully(src: IoBuffer, length: Int)
    override fun fill(n: Long, v: Byte)
    override fun append(c: Char): BytePacketBuilderBase
    override fun append(csq: CharSequence?): BytePacketBuilderBase
    override fun append(csq: CharSequence?, start: Int, end: Int): BytePacketBuilderBase
    open fun writePacket(p: ByteReadPacket)
    fun writePacket(p: ByteReadPacket, n: Int)
    fun writePacket(p: ByteReadPacket, n: Long)
    override fun append(csq: CharArray, start: Int, end: Int): Appendable
    fun writeStringUtf8(s: String)
    fun writeStringUtf8(cs: CharSequence)
    fun release()
    override fun `$prepareWrite$`(n: Int): IoBuffer
    override fun `$afterWrite$`()
    fun reset()
}

val PACKET_MAX_COPY_SIZE: Int
```

## Reading packets

```kotlin
class ByteReadPacket(head: IoBuffer, pool: ObjectPool<IoBuffer>) : Input {
    companion object {
        val Empty: ByteReadPacket
        val ReservedSize: Int
    }
    
    var byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN

    val remaining: Long
    val isEmpty: Boolean
    val isNotEmpty: Boolean
    val endOfInput: Boolean
    
    fun canRead(): Boolean
    fun hasBytes(n: Int): Boolean
    fun copy(): ByteReadPacket
    fun release()
    fun close()

    fun readByte(): Byte 
    fun readShort(): Short
    fun readFloat(): Float
    fun readDouble(): Double
    fun readInt(): Int
    fun readLong(): Long
    fun readAvailable(dst: ByteArray): Int
    fun readAvailable(dst: ByteArray, offset: Int, length: Int): Int
    fun readFully(dst: ByteArray, offset: Int, length: Int)
    fun discard(n: Int)
    fun discardExact(n: Int)
    fun readFully(dst: NumArray, offset: Int, length: Int)
    fun readAvailable(dst: NumArray, offset: Int, length: Int): Int
    fun readFully(dst: IoBuffer, length: Int)
    fun readAvailable(dst: IoBuffer, length: Int): Int
    fun tryPeek(): Int
    fun discard(n: Long): Long
    fun readCbuf(cbuf: CharArray, off: Int, len: Int): Int
    fun readText(out: Appendable, min: Int = 0, max: Int = Int.MAX_VALUE): Int
    fun readTextExact(out: Appendable, exactCharacters: Int)
    fun readText(min: Int = 0, max: Int = Int.MAX_VALUE): String
    fun readTextExact(exactCharacters: Int): String
}

class EOFException(message: String) : Exception
```
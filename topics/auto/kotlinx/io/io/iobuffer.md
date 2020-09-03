[//]: # (title: IoBuffer)
[//]: # (caption: IoBuffer)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

`IoBuffer`s are fixed size buffers in memory implementing the [Input and Output](/kotlinx/io/io/input-output.html) interfaces:

```kotlin
// To make this less verbose:
//   NumArray = ByteArray | ShortArray | CharArray | IntArray | LongArray | FloatArray | DoubleArray 

class IoBuffer : Input, Output {
    var next: IoBuffer?
    var attachment: Any?

    val capacity: Int
    val startGap: Int
    val endGap: Int
    val readRemaining: Int
    val writeRemaining: Int
 
    fun canRead(): Boolean
    fun canWrite(): Boolean
    fun reserveStartGap(n: Int)
    fun reserveEndGap(n: Int)

    var byteOrder: ByteOrder

    fun readByte(): Byte
    fun readShort(): Short
    fun readInt(): Int
    fun readLong(): Long
    fun readFloat(): Float
    fun readDouble(): Double

    fun readFully(dst: NumArray, offset: Int, length: Int)
    fun readFully(dst: IoBuffer, length: Int)

    fun readAvailable(dst: NumArray, offset: Int, length: Int): Int
    fun readAvailable(dst: IoBuffer, length: Int): Int

    fun tryPeek(): Int
    fun discard(n: Long): Long
    fun discardExact(n: Int)

    fun writeByte(v: Byte)
    fun writeShort(v: Short)
    fun writeInt(v: Int)
    fun writeLong(v: Long)
    fun writeFloat(v: Float)
    fun writeDouble(v: Double)

    fun writeFully(src: NumArray, offset: Int, length: Int)
    fun writeFully(src: IoBuffer, length: Int)

    fun appendChars(csq: CharArray, start: Int, end: Int): Int
    fun appendChars(csq: CharSequence, start: Int, end: Int): Int

    fun append(c: Char): Appendable
    fun append(csq: CharSequence?): Appendable
    fun append(csq: CharSequence?, start: Int, end: Int): Appendable
    fun append(csq: CharArray, start: Int, end: Int): Appendable

    fun fill(n: Long, v: Byte)

    fun write(array: ByteArray, offset: Int, length: Int)
    fun writeBuffer(src: IoBuffer, length: Int): Int

    fun pushBack(n: Int)
    
    fun resetForWrite()
    fun resetForWrite(limit: Int)
    fun resetForRead()
    fun isExclusivelyOwned(): Boolean
    fun makeView(): IoBuffer
    fun release(pool: ObjectPool<IoBuffer>)

    fun flush()

    fun close()

    companion object {
        val Empty: IoBuffer
        val Pool: ObjectPool<IoBuffer>
        val NoPool: ObjectPool<IoBuffer>
        val EmptyPool: ObjectPool<IoBuffer>
    }
}
```
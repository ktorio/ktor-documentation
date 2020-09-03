[//]: # (title: Tools)
[//]: # (caption: Tools)
[//]: # (category: kotlinx)
[//]: # (toc: false)
[//]: # (ktor_version_review: 1.0.0)

## ObjectPool
{id="objectpool "}

`ObjectPool` is a general purpose lock-free concurrent-safe object pool. It is also leak-safe:
all object that haven't been recycled but collected by GC do not cause any issues with a pool but only an allocation penalty.
Note that it doesn't mean that leaking object will not cause any issues at all as lost objects could
hold some native or external resources. The only guarantee is that `ObjectPool` is not going to break
if there are lost objects.

```kotlin
interface ObjectPool<T : Any> : Closeable {
    val capacity: Int
    fun borrow(): T
    fun recycle(instance: T)
    fun dispose()
    fun close() = dispose()
}

inline fun <T : Any, R> ObjectPool<T>.useBorrowed(block: (T) -> R): R // alias of useInstance
inline fun <T : Any, R> ObjectPool<T>.useInstance(block: (T) -> R): R
```

```kotlin
val ExampleIntArrayPool = object : DefaultPool<IntArray>(ARRAY_POOL_SIZE) {
    override fun produceInstance(): IntArray = IntArray(ARRAY_SIZE)
}

class ExampleDirectByteBufferPool(val bufferSize: Int, size: Int) : DefaultPool<ByteBuffer>(size) {
    override fun produceInstance(): ByteBuffer = java.nio.ByteBuffer.allocateDirect(bufferSize)

    override fun clearInstance(instance: ByteBuffer): ByteBuffer {
        instance.clear()
        return instance
    }

    override fun validateInstance(instance: ByteBuffer) {
        require(instance.isDirect)
        require(instance.capacity() == bufferSize)
    }
}
```
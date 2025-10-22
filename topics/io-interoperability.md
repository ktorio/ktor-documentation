[//]: # (title: I/O Interoperability)

<show-structure for="chapter" depth="2"/>

<link-summary>
Learn how to use Ktor's I/O primitives with external types.
</link-summary>

Ktor supports non-blocking, asynchronous I/O built on top of [`kotlinx-io`](https://github.com/Kotlin/kotlinx-io), a
multiplatform Kotlin library providing basic I/O primitives. This allows you to stream HTTP request and response bodies,
read or write files, and process data incrementally without loading it all into memory.

If you're working with external libraries or platforms that use different I/O models, you can use a set of adapters to
interoperate with:

- `kotlinx-io` types such as `RawSource` and `RawSink`
- Java I/O types such as `OutputStream`

This page describes how to convert between Ktor’s I/O primitives (
[`ByteReadChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html),
[`ByteWriteChannel`](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-write-channel/index.html)) and these external
types.

## Convert `ByteReadChannel` to `RawSource`

To convert a `ByteReadChannel` into a `RawSource`, use the `.asSource()` extension function:

```kotlin
val channel: ByteReadChannel = response.bodyAsChannel()
val source: RawSource = channel.asSource()
```

## Convert `ByteWriteChannel` to `RawSink`

To convert a suspending `ByteWriteChannel` into a `RawSink`, use the `.asSink()` extension function:

```kotlin
val channel: ByteWriteChannel = ...
val sink: RawSink = channel.asSink()
```

The `RawSink` produced by this adapter uses `runBlocking` internally when flushing data, so flush operations may block
the calling thread.

## Convert `RawSink` to `ByteWriteChannel`

To wrap a `RawSink` as a suspending `ByteWriteChannel`, use the `.asByteWriteChannel()` extension function:

```kotlin
val sink: RawSink = ...
val channel: ByteWriteChannel = sink.asByteWriteChannel()

channel.writeByte(42)
channel.flushAndClose()
```

This enables asynchronous writing to sinks from suspending functions. The returned channel is buffered. Use `.flush()`
or `.flushAndClose()` to ensure that all data is written.

## Convert `OutputStream` to `ByteWriteChannel`

To convert a Java `OutputStream` to a `ByteWriteChannel`, use the `.asByteWriteChannel()` extension function:

```kotlin
val outputStream: OutputStream = FileOutputStream("output.txt")
val channel: ByteWriteChannel = outputStream.asByteWriteChannel()

channel.writeFully("Hello, World!".toByteArray())
channel.flushAndClose()
```

All operations on the `ByteWriteChannel` are buffered. The underlying `OutputStream` receives data only when `.flush()`
is called on the `ByteWriteChannel`.
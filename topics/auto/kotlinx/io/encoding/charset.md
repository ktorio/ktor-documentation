[//]: # (title: Charset)
[//]: # (caption: Charset)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

A Charset is a class that is in charge of serializing and deserializing characters.
It mimics the Java [java.nio.charset.Charset](https://docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html) class,
and it's equivalent in .NET would be the [Encoding](https://msdn.microsoft.com/es-es/library/system.text.encoding(v=vs.110).aspx) class.

Some example of charsets could be `UTF-8`, `ASCII`, `ISO-8859-1`(`LATIN1`), or `SHIFT_JIS`, among others.

## Instantiating a Charset

`kotlinx-io` has an object called `Charsets` with some common used charsets (right now only `UTF-8`):

```kotlin
val charset = Charsets.UTF_8
```

It is also possible to construct a charset from its name:

```kotlin
val charset = Charset.forName("utf-8")
```

## Encoding and decoding Strings

The easiest way for encoding/decoding Strings using charsets is to use the extension methods available for `String` and `ByteArray`.

For example, if we have a `ByteArray` representing a UTF-8 encoded string, you can:

```kotlin
val utf8Encoded: ByteArray = byteArrayOf(0xE4.toByte(), 0xBB.toByte(), 0x8A.toByte(), 0xE6.toByte(), 0x97.toByte(), 0xA5.toByte(), 0xE3.toByte(), 0x81.toByte(), 0xAF.toByte(), 0xEF.toByte(), 0xBC.toByte(), 0x81.toByte())
val string = utf8Encoded.toString(Charsets.UTF_8)
```

You can also use an extension constructor for it:

```kotlin
val string = String(utf8Encoded, charset = Charsets.UTF_8)
val substring = String(utf8Encoded, 0, 10, charset = Charsets.UTF_8)
```

And from a `String`, you can generate a ByteArray representing it in the specific charset:

```kotlin
val string = "今日は！"
val utf8Encoded = string.toByteArray(Charsets.UTF_8)
```

## Performance-aware API

In order to be able to reduce copying, reuse of object and memory and reduce garbage collection, the Charset API
offers a way to create encoders and decoders offering APIs to encode from `CharSequence` to `Output`, and vice versa.

For encoding a slice of a `String` without slicing it into an `Output`, you can:

```kotlin
val output: Output
val charset = Charsets.UTF_8
val charsetEncoder = charset.newEncoder() 
charsetEncoder.encode("HELLO", 1, 2, output)
``` 

## Further details about Charsets

Some charsets represent characters as single-byte, other represents characters with several fixed bytes per characters,
and others are variable.
Not all the charsets can represent the whole Unicode character set.

The most widely used charset is UTF-8, which allows to represent most 7-bit ASCII characters as single byte,
but allows to represent the whole Unicode character set. By being encoded by a variable length of bytes,
getting the length of a `String` represented as UTF-8 requires decoding the whole content,
thus requiring linear time for seeking, slicing and getting length.

JavaScript exposes Strings with 32-bit CodePoints, while Java and Native exposes 16-bit Chars using [Surrogate Pairs](https://en.wikipedia.org/wiki/UTF-16#U+10000_to_U+10FFFF) for higher values.  

For now, kotlinx-io only implements `UTF_8`, but allows to add custom charsets by extending the `Charset` class.

## Exposed API

```kotlin
object Charsets {
    val UTF_8: Charset
}

class Charset {
    companion object {
        fun forName(name: String): Charset
    }

    val name: String

    fun newEncoder(): CharsetEncoder
    fun newDecoder(): CharsetDecoder
}

class CharsetEncoder {
    val charset: Charset
    fun encode(input: CharSequence, fromIndex: Int, toIndex: Int, dst: Output)
    fun encodeToByteArray(input: CharSequence, fromIndex: Int = 0, toIndex: Int = input.length): ByteArray
    fun encodeUTF8(input: ByteReadPacket, dst: Output) // Return type. Why UTF8 ??
    fun encode(input: CharSequence, fromIndex: Int = 0, toIndex: Int = input.length) // Return type. Why UTF8 ??
    fun encodeUTF8(input: ByteReadPacket) // Return type. Why UTF8 ??
}

class CharsetDecoder {
    val charset: Charset
    fun decode(input: Input, max: Int = Int.MAX_VALUE): String
    fun decode(input: Input, dst: Appendable, max: Int): Int
    fun decodeExactBytes(input: Input, inputLength: Int): String
}

class MalformedInputException(message: String) : Throwable

// Missing:
//   - String.toByteArray(charset): ByteArray
//   - ByteArray.toString(charset): String
// Missing registering new charsets available using Charset.forName?
```
[//]: # (title: ByteOrder)
[//]: # (caption: ByteOrder)
[//]: # (category: kotlinx)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

When encoding fixed-size values larger than 8-bits/byte/octet, there are two standard ways of doing so:
by storing the less significant part of the number bytes first (LITTLE endian) or by storing most significant bytes first (BIG endian).

The `nativeOrder()` represents the order your main CPU uses. Native order is usually slightly faster since the processor can avoid a byte swapping operation.

```kotlin
expect enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    companion object {
        fun nativeOrder(): ByteOrder
    }
}
```

## LITTLE Endian

Little endian is the native order of x86 processors and most ARM processors.
This is the most popular native order of CPUs.

* **BENEFIT:** for small numbers, you get the most relevant information first, and can be read in a meaninful way without knowing the bit-width beforehand.  
* **DISADVANTAGE:** cannot be read naturally from left to right.

## BIG Endian

Big endian, also called the network endian or motorola endian.
This is the most used endian order for network protocols.
Some CPUs used it as native endian in the past including Motorla,
some MIPS and ARM, PowerPCs, SPARC among others.

* **BENEFIT:** can be read naturally from left to right even in HEX representation.
* **DISADVANTAGE:** you need to know the width of the data beforehand to be able to meanigful read it.

## Tools

### Converting values between endians:

You can of course, serialize the value in a view or a packet with one endian,
and then deserialize it with another endian.

But there are more efficient ways. For `Short`, `Int` and `Long`, you have:

* `java.lang.Short.reverseBytes(10.toShort())` - `(16 bits, 2 octets) AA BB <-> BB AA`
* `java.lang.Integer.reverseBytes(10)` - `(32 bits, 4 octets) AA BB CC DD <-> DD CC BB AA`
* `java.lang.Long.reverseBytes(10L)` - `(64 bits, 8 octets) AA BB CC DD EE FF GG HH <-> HH GG FF EE DD CC BB AA`

`Float` and `Double` can be reversed by getting their bits and reconstructing from their bits:

* `Float.fromBits(java.lang.Integer.reverseBytes(10f.toBits()))` - `(32 bits, 4 octets)`
* `Double.fromBits(java.lang.Long.reverseBytes(10.0.toBits()))` - `(64 bits, 8 octets)`

> For a Float (32-bits) (and similarly with Double but increasing exponent and fraction bits), there are a sign (1 bit), and exponent (8 bits) and a fraction (23 bits):
> `seeeeeeeefffffffffffffffffffffff` - s=sign, e=exponent, f=fraction.
> In octets: `seeeeeee-efffffff-ffffffff-ffffffff`, and thus reversing bytes would produce very strange
> floating point values.

### Extensions methods in pure Kotlin for byte-reversing integral types
{ kotlin-byte-reversing }

When using the JVM, each boxed integral type except Byte, provides a `reverseBytes` method (for example `java.lang.Integer.reverseBytes`).
For common and other Kotlin targets, you can use these extension methods:

```kotlin
fun Short.reverseBytes(): Short {
	val v0 = ((this.toInt() ushr 0) and 0xFF)
	val v1 = ((this.toInt() ushr 8) and 0xFF)
	return ((v1 and 0xFF) or (v0 shl 8)).toShort()
}

fun Int.reverseBytes(): Int {
	val v0 = ((this ushr 0) and 0xFF)
	val v1 = ((this ushr 8) and 0xFF)
	val v2 = ((this ushr 16) and 0xFF)
	val v3 = ((this ushr 24) and 0xFF)
	return (v0 shl 24) or (v1 shl 16) or (v2 shl 8) or (v3 shl 0)
}

fun Long.reverseBytes(): Long {
	val v0 = (this ushr 0).toInt().reverseBytes().toLong() and 0xFFFFFFFFL
	val v1 = (this ushr 32).toInt().reverseBytes().toLong() and 0xFFFFFFFFL
	return (v0 shl 32) or (v1 shl 0)
}
```
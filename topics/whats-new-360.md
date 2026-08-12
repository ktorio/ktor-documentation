[//]: # (title: What's new in Ktor 3.6.0)

<show-structure for="chapter,procedure" depth="2"/>

[//]: # (TODO: Ensure release date is correct)
_[Released: August 26, 2026](releases.md#release-details)_

Ktor 3.6.0 delivers a range of improvements across server and client. Highlights of this feature release include:

[//]: # (TODO: Add a bullet list with highlights)

## Ktor Server

### Additional type support for request parameters

Ktor 3.6.0 expands the set of types supported by default when converting request parameters to typed values.

The following types are now supported:

* `Uuid`
* `Byte`
* `java.lang.Byte`
* `UByte`
* `UShort`
* `UInt`
* `ULong`

For example, you can retrieve a `Uuid` parameter directly inside a route handler through property delegation:

```kotlin
get {
    val uuid: Uuid by call.parameters
}
```


## Ktor Client
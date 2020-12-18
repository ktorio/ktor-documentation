[//]: # (title: Serializers)

## Serializers
{id="serializer"}

Server session serializers provide a way to pass session objects between client and server. They are used in both stateless and stateful scenarios.
In the stateless scenario, the whole session is passed with every request. Usually it is done by sending it inside of cookies.
When the stateful scenario is used, a session is never exposed to clients but a session id. A session object is stored somewhere on server-side, for example in memory or on disk.

When configuring [sessions](sessions.md), you get the default session serializer or a [custom serializer](#extending-serializers) could be provided.

### The default session serializer
{id="default-session-serializer"}

The default session serializer is used when no serializer specified explicitly and constructed with `defaultSessionSerializer()` function. It has no configuration and uses the text format described below. Unlike general purpose serializers, it is optimized for size to produce short one-line serialized strings. Although, it has limitations:
- session class should be a regular class, a sealed class with non-abstract sub-classes;
- no polymorphic serialization supported except for the limited support of sealed classes, abstract classes are not allowed;
- session class properties should have type one of the following:
  * primitive types;
  * `String`;
  * enum class;
  * collection `List`, `Set`, `Map`;
  * data class;
  * sealed class;
  * object;
- only the common collections supported: List, Set and Map;
- type parameters of collections should be primitive or `String`;
- inner classes are not supported;
- cyclic object graphs are not supported.

The default text format is a form-url-encoded bundle where names are properties and values are encoded with a type prefix starting with a `#` hash character. The character following the hash character points to a value type. For example, `#i` points to the `Int` type.

Example of an encoded data class having a single integer property named "test":
`test=#i1`

Note: sealed classes support is introduced since ktor 1.5.0. Before this version, a session class should have no properties having custom class types except for enum classes.

## Custom serializers
{id="extending-serializers"}

The Sessions API provides a `SessionSerializer` interface, that looks like this:

```kotlin
interface SessionSerializer<T> {
    fun serialize(session: T): String
    fun deserialize(text: String): T
}
```

This interface is for a generic serializer, and you can install it like this:

```kotlin
cookie<MySession>("NAME") {
    serializer = MyCustomSerializer()
}
```

So for example you can create a JSON session serializer using Gson:

```kotlin
class GsonSessionSerializer<T>(
    val type: java.lang.reflect.Type,
    val gson: Gson = Gson(),
    configure: Gson.() -> Unit = {}
) : SessionSerializer<T> {
    init {
        configure(gson)
    }

    override fun serialize(session: T): String = gson.toJson(session)
    override fun deserialize(text: String): T = gson.fromJson(text, type) as T
}
```

And configuring it:

```kotlin
cookie<MySession>("NAME") {
    serializer = GsonSessionSerializer(MySession::class.java)
}
```

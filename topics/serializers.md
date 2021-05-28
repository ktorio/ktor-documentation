[//]: # (title: Serializers)

[Sessions](sessions.md) allow you to choose whether to pass the entire payload between the client and server or store session data on the server and pass only a session ID. In both cases, Ktor uses [the default serializer](#default-session-serializer) to serialize session data. You can also provide your own [custom serializer](#extending-serializers) and, for example, convert session data to JSON.

## Default serializer
{id="default-session-serializer"}

The default session serializer is used when no serializer is specified explicitly and constructed with [`defaultSessionSerializer`](https://api.ktor.io/latest/io.ktor.sessions/default-session-serializer.html) function. It has no configuration and uses the [text format](#text-format). Unlike general purpose serializers, it is optimized for size to produce short one-line serialized strings.

### Limitations
{id="limitations"}

The default session serializer has the following limitations:

- Session class should be a regular or sealed class.
- Polymorphic serialization is limited to only sealed classes: abstract classes are not allowed.
- Properties of a session class should have one of the following types:
  * primitive types;
  * `String`;
  * enum class;
  * collection classes such as `List`, `Set`, `Map`;
  * data class;
  * sealed class;
  * object;
- Type parameters of collections should be primitive or `String`.
- Inner classes are not supported.
- Cyclic object graphs are not supported.

### Text format
{id="text-format"}

The default serializer converts data into a [form-url-encoded](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/POST) bundle where names are properties and values are encoded with a type prefix starting with a `#` hash character. The character following the hash character points to a value type. For example, a data class with a single integer property is encoded to `test=#i1`.

## Custom serializers
{id="extending-serializers"}

The Sessions API provides the [SessionSerializer](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.sessions/-session-serializer/index.html) interface that looks like this:

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

For example, you can create a JSON session serializer using [Gson](https://github.com/google/gson)...

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

...and configure it:

```kotlin
cookie<MySession>("NAME") {
    serializer = GsonSessionSerializer(MySession::class.java)
}
```

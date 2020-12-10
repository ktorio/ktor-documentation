[//]: # (title: Serializers)

<include src="lib.md" include-id="outdated_warning"/>

## Serializers
{id="serializer"}

You can specify a custom serializer with:

```kotlin
application.install(Sessions) {
    cookie<MySession>("SESSION") {
        serializer = MyCustomSerializer()
    }
} 
```

If you do not specify any serializer, it will use one with an internal optimized format.

### SessionSerializerReflection
{id="SessionSerializerReflection"}

This is the default serializer, when no serializer is specified:

```kotlin
cookie<MySession>("SESSION") {
    serializer = autoSerializerOf()
}
```

## Custom serializers
{id="extending-serializers"}

The Sessions API provides a `SessionSerializer` interface, that looks like this:

```kotlin
interface SessionSerializer {
    fun serialize(session: Any): String
    fun deserialize(text: String): Any
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
class GsonSessionSerializer(
    val type: java.lang.reflect.Type, val gson: Gson = Gson(), configure: Gson.() -> Unit = {}
) : SessionSerializer {
    init {
        configure(gson)
    }

    override fun serialize(session: Any): String = gson.toJson(session)
    override fun deserialize(text: String): Any = gson.fromJson(text, type)
}
```

And configuring it:

```kotlin
cookie<MySession>("NAME") {
    serializer = GsonSessionSerializer(MySession::class.java)
}
```
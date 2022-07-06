[//]: # (title: Attributes)

<include src="lib.xml" element-id="outdated_warning"/>

Ktor offers an `Attributes` class that acts as a small typed instance container/dependency injector.

For the server, the `ApplicationCall` contains an `attributes` property including an instance of this class (`call.attributes`).
The lifespan of this container is the call: beginning when the request has started and ending when the response has been sent.

You can set as many attributes as required per call on an interceptor and retrieve them later in another interceptor.

In the case of the client, the `HttpRequest` also contains an `attributes` property.
So from a `HttpClientCall` instance, you can access the attributes with `call.request.attributes`.

## Basic usage

It is possible to define your own typed attributes by creating instances of the `AttributeKey` class like this:

```kotlin
// Declared as a global property
val MyAttributeKey = AttributeKey<Int>("MyAttributeKey")
```

You can later set the attributes with:

```kotlin
attributes.put(MyAttributeKey, 10)
```

And retrieve them in another interceptor by calling:

```kotlin
attributes.get(MyAttributeKey)
```

## API reference

The full interface for this class looks like:

```kotlin
interface Attributes {
    operator fun <T : Any> get(key: AttributeKey<T>): T
    fun <T : Any> getOrNull(key: AttributeKey<T>): T?
    operator fun contains(key: AttributeKey<*>): Boolean
    fun <T : Any> put(key: AttributeKey<T>, value: T)
    fun <T : Any> remove(key: AttributeKey<T>)
    fun <T : Any> take(key: AttributeKey<T>): T = get(key).also { remove(key) }
    fun <T : Any> takeOrNull(key: AttributeKey<T>): T? = getOrNull(key).also { remove(key) }
    fun <T : Any> computeIfAbsent(key: AttributeKey<T>, block: () -> T): T
    val allKeys: List<AttributeKey<*>>
}
```
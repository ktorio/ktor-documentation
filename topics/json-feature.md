[//]: # (title: Json)

<include src="lib.md" include-id="outdated_warning"/>

Processes the request and the response payload as JSON, serializing
and de-serializing them using a specific `serializer: JsonSerializer`.

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(JsonFeature)
}
```

You have a [full example using JSON](examples.md#example-json).

>To use this feature with Kotlin/JS, you need to include the `io.ktor:ktor-client-json-js` artifact.
>
{type="note"}

## Serializers

The `JsonFeature` has a default serializer(implicitly obtained or by calling `defaultSerializer()`)
based on a ServiceLoader on JVM(supporting Gson or Jackson depending on the artifact included),
and a serializer based on [kotlinx.serialization](kotlin_serialization.md) for Native as well as for JavaScript.

You can also get the default serializer by calling `io.ktor.client.features.json.defaultSerializer()`

### Gson {id="gson"}

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(JsonFeature) {
        serializer = GsonSerializer()
    }
}
```

>To use this feature, you need to include `io.ktor:ktor-client-gson` artifact.
>
{type="note"}

### Jackson {id="jackson"}

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(JsonFeature) {
        serializer = JacksonSerializer()
    }
}
```

>To use this feature, you need to include `io.ktor:ktor-client-jackson` artifact.
>
{type="note"}

### Kotlinx.Serialization {id="kotlinx-serialization"}

```kotlin
val client = HttpClient(HttpClientEngine) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}
```

>To use this feature, you need to include `io.ktor:ktor-client-serialization-jvm` artifact on the JVM and `io.ktor:ktor-client-serialization-native` on iOS.
>
{type="note"}

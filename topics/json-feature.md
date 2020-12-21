[//]: # (title: Json)

[JsonFeature](https://api.ktor.io/%ktor_version%/io.ktor.client.features.json/-json-feature/index.html) can be used to serialize/deserialize JSON data when sending [requests](request.md) and receiving [responses](response.md). This functionality is provided by either the built-in `kotlinx.serialization` library or external ones, such as `Gson` and `Jackson`. 

> On the server side, Ktor provides the [ContentNegotiation](serialization.md) feature for serializing/deserializing the content.


## Add Dependencies {id="add_dependencies"}
Before installing `JsonFeature`, you need to add a dependency for a desired serializer. Ktor can choose a [default serializer](#todo) automatically.

### Gson and Jackson: JVM {id="jvm_dependency"}
Gson:

<var name="artifact_name" value="ktor-client-gson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

Jackson:

<var name="artifact_name" value="ktor-client-jackson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


### kotlinx: JVM, iOS, JS {id="kotlinx_dependency"}

Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

JVM:
<var name="artifact_name" value="ktor-client-serialization-jvm"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

iOS:
<var name="artifact_name" value="ktor-client-serialization-native"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

JS:
<var name="artifact_name" value="ktor-client-serialization-js"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Install JsonFeature {id="install_feature"}
To install `JsonFeature`, pass it to the `install` function inside a [client configuration block](client.md#client-configuration):
```kotlin
val client = HttpClient(CIO) {
    install(JsonFeature)
}
```
Now you can [configure](#configure_serializer) the required JSON serializer.


## Configure a Serializer {id="configure_serializer"}
Specify the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer()
}
```

### Gson {id="gson"}

Assign `GsonSerializer` to the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = GsonSerializer()
}
```
Inside the `GsonSerializer` constructor, you can access [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html) API, for example: 
```kotlin
install(JsonFeature) {
    serializer = GsonSerializer() {
        setPrettyPrinting()
        disableHtmlEscaping()
    }
}
```

### Jackson {id="jackson"}

Assign `JacksonSerializer` to the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = JacksonSerializer()
}
```
Inside the `JacksonSerializer` constructor, you can access the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html) API...
```kotlin
install(JsonFeature) {
    serializer = JacksonSerializer() {
        enable(SerializationFeature.INDENT_OUTPUT)
        dateFormat = DateFormat.getDateInstance()
    }
}
```
... or pass the `ObjectMapper` instance directly to the `JacksonSerializer` constructor.

### kotlinx {id="kotlinx"}

Assign `KotlinxSerializer` to the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer()
}
```
Inside the `json` method, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
        prettyPrint = true
        isLenient = true
    })
}
```
> This change very often, but with Kotlin 1.4.10 and Ktor 1.4.1 you need to pass a kotlinx Json (be careful because there is also a io.ktor.client.features.json.Json, I used an import alias to distinguish them because I needed both import kotlinx.serialization.json.Json as KotlinJson)

To learn how to receive and send data, see [](serialization.md#receive_send_data).



## Receive and Send Data {id="receive_send_data"}
### Create a Data Class {id="create_data_class"}
To deserialize received data into an object, you need to create a data class, for example:
```kotlin
data class Customer(val id: Int, val firstName: String, val lastName: String)
```
If you use [](#kotlinx), make sure that this class has the `@Serializable` annotation:
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)
```

### Send Data {id="send_data"}
[request](request.md)

If you install the JsonFeature, and set the content type to application/json you can use arbitrary instances as the body, and they will be serialized as JSON:

```kotlin
client.post<Unit>() {
    url("http://127.0.0.1:8080/customer")
    contentType(ContentType.Application.Json)
    body = Customer(1, "Jet", "Brains")
}
```

### Receive Data {id="receive_data"}
[response](response.md)

If JsonFeature is configured, and the server returns the header Content-Type: application/json, you can also specify a class for deserializing it.
To receive and convert a content for a response, call the `get` method that accepts a data class as a parameter:
```kotlin
val customer = client.get<Customer>("http://127.0.0.1:8080/customer")
```

Receive
```kotlin
val savedUserInfo = client.post<UserInfo> {
    url("http://127.0.0.1:8080/customer")
    contentType(ContentType.Application.Json)
    body = Customer(1, "Jet", "Brains")
}
```




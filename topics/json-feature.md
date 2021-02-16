[//]: # (title: Json)

[JsonFeature](https://api.ktor.io/%ktor_version%/io.ktor.client.features.json/-json-feature/index.html) can be used to serialize/deserialize JSON data when sending [requests](request.md) and receiving [responses](response.md). This functionality is provided for JVM by using the `Gson`/`Jackson` libraries and for [Kotlin Multiplatform](http-client_multiplatform.md) by using `kotlinx.serialization`.

> On the server, Ktor provides the [ContentNegotiation](serialization.md) feature for serializing/deserializing content.


## Add Dependencies {id="add_dependencies"}
Before installing `JsonFeature`, you need to add a dependency for the desired serializer. If your project targets only JVM, you can add [Gson or Jackson](#jvm_dependency) dependency. For [multiplatform](http-client_multiplatform.md) projects, use the [kotlinx.serialization](#kotlinx_dependency) library. Depending on the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer) explicitly and configure it.


### JVM: Gson and Jackson  {id="jvm_dependency"}
To use Gson, add the following artifact to the build script:
<var name="artifact_name" value="ktor-client-gson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

For Jackson, add the following dependency:
<var name="artifact_name" value="ktor-client-jackson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


### JVM, iOS, JS: kotlinx {id="kotlinx_dependency"}

For multiplatform projects, you can use the `kotlinx.serialization` library. You can add it to the project as follows:
1. Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.
1. Add the `ktor-client-serialization` dependency:
   
<var name="artifact_name" value="ktor-client-serialization"/>
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

Depending on the [included artifacts](#add_dependencies), Ktor chooses a default serializer automatically. You can get this serializer by calling the [io.ktor.client.features.json.defaultSerializer](https://api.ktor.io/%ktor_version%/io.ktor.client.features.json/default-serializer.html) function.

To specify the required serializer explicitly, use the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer()
}
```
For the selected serializer, you can access its API and adjust a configuration. Let's see how to do this.


### Gson {id="gson"}

To use Gson, assign the `GsonSerializer` instance to the `serializer` property:
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

To use Jackson, assign the `JacksonSerializer` instance to the `serializer` property:
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

To use Kotlin serialization, assign the `KotlinxSerializer` instance to the `serializer` property:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer()
}
```
Inside the `KotlinxSerializer` constructor, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
install(JsonFeature) {
    serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
        prettyPrint = true
        isLenient = true
    })
}
```



## Receive and Send Data {id="receive_send_data"}
### Create a Data Class {id="create_data_class"}
To deserialize received data into an object, you need to create a data class, for example:
```kotlin
data class Customer(val id: Int, val firstName: String, val lastName: String)
```
If you use [kotlinx.serialization](#kotlinx), make sure that this class has the `@Serializable` annotation:
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)
```

### Send Data {id="send_data"}

To send a [class instance](#create_data_class) within a [request](request.md) body as JSON, assign this instance to the `body` property and set the content type to `application/json` by calling `contentType`:

```kotlin
client.post<Unit>() {
    url("http://127.0.0.1:8080/customer")
    contentType(ContentType.Application.Json)
    body = Customer(1, "Jet", "Brains")
}
```

### Receive Data {id="receive_data"}

When a server sends a [response](response.md) with the `application/json` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of the required request method, for example:
```kotlin
val customer = client.get<Customer>("http://127.0.0.1:8080/customer")
```

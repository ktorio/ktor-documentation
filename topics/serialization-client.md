[//]: # (title: Serialization)

<microformat>
<var name="example_name" value="content-negotiation-client"/>
<include src="lib.md" include-id="download_example"/>
</microformat>


[ContentNegotiation](https://api.ktor.io/%ktor_version%/io.ktor.client.features.contentnegotiation/-content-negotiation/index.html) can be used to serialize/deserialize data when sending [requests](request.md) and receiving [responses](response.md). This functionality is provided for JVM by using the `Gson`/`Jackson` libraries and for [Kotlin Multiplatform](http-client_multiplatform.md) by using `kotlinx.serialization`.

> On the server, Ktor provides the [ContentNegotiation](serialization-server.md) feature for serializing/deserializing content.


## Install ContentNegotiation {id="install_feature"}

<var name="feature_name" value="ContentNegotiation"/>
<include src="lib.md" include-id="install_feature"/>


## Register a converter {id="register_converter"}

To register a converter for a specified `Content-Type`, you need to call the [register](https://api.ktor.io/%ktor_version%/io.ktor.client.features/-content-negotiation/-config/register.html) method. In the example below, two [custom converters](#implement_custom_converter) are registered to deserialize `application/json` and `application/xml` data:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, CustomJsonConverter())
    register(ContentType.Application.Xml, CustomXmlConverter())
}
```

### Built-in converters {id="built_in_converters"}
Ktor provides the set of built-in converters for handing various content types without writing your own logic:

* [Gson](gson.md) for JSON
* [Jackson](jackson.md) for JSON
* [kotlinx.serialization](kotlin_serialization.md) for JSON, Protobuf, CBOR, and so on

See a corresponding topic to learn how to install the required dependencies, register, and configure a converter.


## Receive and send data {id="receive_send_data"}
### Create a data class {id="create_data_class"}

To receive and send data, you need to have a data class, for example:
```kotlin
data class Customer(val firstName: String, val lastName: String)
```
If you use [kotlinx.serialization](#kotlinx), make sure that this class has the `@Serializable` annotation:
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val firstName: String, val lastName: String)
```
To learn more about kotlinx.serialization, see the [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

### Send data {id="send_data"}

To send a [class instance](#create_data_class) within a [request](request.md) body as JSON, assign this instance to the `body` property and set the content type to `application/json` by calling `contentType`:

```kotlin
```
{src="snippets/_misc_client/PostMethodWithObject.kt"}

### Receive data {id="receive_data"}

When a server sends a [response](response.md) with the `application/json` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of the required request method, for example:
```kotlin
val customer = client.get<Customer>("http://127.0.0.1:8080/customer")
```


## Implement a custom converter {id="implement_custom_converter"}

In Ktor, you can write your own [converter](#register_converter) for serializing/deserializing data. To do this, you need to implement the [ContentConverter](https://api.ktor.io/%ktor_version%/io.ktor.shared.serialization/-content-converter/index.html) interface:
```kotlin
interface ContentConverter {
    suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any): OutgoingContent?    
    suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any?
}
```
Take a look at the [GsonConverter](https://github.com/ktorio/ktor/blob/main/ktor-shared/ktor-shared-serialization-gson/jvm/src/GsonConverter.kt) class as an implementation example.  


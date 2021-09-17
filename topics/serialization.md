[//]: # (title: Content negotiation and serialization)

The [ContentNegotiation](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-content-negotiation/index.html) plugin (previously known as feature) serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in the specific format, which is provided by either the built-in `kotlinx.serialization` library or external ones, such as `Gson` and `Jackson`, amongst others.


## Add dependencies {id="add_dependencies"}
To use `ContentNegotiation`, you need to include the `ktor-server-content-negotiation` artifact in the build script:

<var name="artifact_name" value="ktor-server-content-negotiation"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

Note that content converters require additional artifacts. For example, [kotlinx.serialization](kotlin_serialization.md#add_dependencies) requires the `ktor-shared-serialization-kotlinx` dependency.


## Install ContentNegotiation {id="install_feature"}

<var name="feature_name" value="ContentNegotiation"/>
<include src="lib.xml" include-id="install_feature"/>


## Register a converter {id="register_converter"}

To register a converter for a specified `Content-Type`, you need to call the [register](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-content-negotiation/-configuration/register.html) method. In the example below, two [custom converters](#implement_custom_converter) are registered to deserialize `application/json` and `application/xml` data:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, CustomJsonConverter())
    register(ContentType.Application.Xml, CustomXmlConverter())
}
```

### Built-in converters {id="built_in_converters"}
Ktor provides the set of built-in converters for handing various content types without writing your own logic:

* [kotlinx.serialization](kotlin_serialization.md) for JSON, Protobuf, CBOR, and so on
* [Gson](gson.md) for JSON
* [Jackson](jackson.md) for JSON

See a corresponding topic to learn how to install the required dependencies, register, and configure a converter.


## Receive and send data {id="receive_send_data"}

### Create a data class {id="create_data_class"}
To deserialize received data into an object, you need to create a data class, for example:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="14"}

If you use [kotlinx.serialization](kotlin_serialization.md), make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="10,12-14"}

### Receive data {id="receive_data"}
To receive and convert a content for a request, call the [receive](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.request/receive.html) method that accepts a data class as a parameter:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="38-42"}

The `Content-Type` of the request will be used to choose a [converter](#register_converter) for processing the request. The example below shows a sample [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) request containing JSON data that will be converted to a `Customer` object on the server side:

```HTTP
```
{src="snippets/json-kotlinx/post.http"}

You can find the full example here: [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/json-kotlinx).

### Send data {id="send_data"}
To pass a data object in a response, you can use the [respond](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.response/respond.html) method:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="32-36"}

In this case, Ktor uses the `Accept` header to choose the required [converter](#register_converter). You can find the full example here: [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/json-kotlinx).



## Implement a custom converter {id="implement_custom_converter"}

In Ktor, you can write your own [converter](#register_converter) for serializing/deserializing data. To do this, you need to implement the [ContentConverter](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-content-converter/index.html) interface:
```kotlin
interface ContentConverter {
    suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any): OutgoingContent?
    suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any?
}
```
Take a look at the [GsonConverter](https://github.com/ktorio/ktor/blob/main/ktor-shared/ktor-shared-serialization-gson/jvm/src/GsonConverter.kt) class as an implementation example.  



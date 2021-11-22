[//]: # (title: WebSockets serialization)

<microformat>
<var name="example_name" value="server-websockets-serialization"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Similarly to the [ContentNegotiation](serialization.md) plugin, WebSockets allow you to serialize/deserialize text frames in a specific format. Ktor supports the following formats out-of-the-box: JSON, XML, and CBOR.


## Add dependencies {id="add_dependencies"}

<include src="serialization.md" include-id="serialization_dependency"/>


## Configure a serializer {id="configure_serializer"}

Ktor supports the following formats out-of-the-box: [JSON](#register_json), [XML](#register_xml), [CBOR](#register_cbor).

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON serializer in your application, create a `KotlinxWebsocketSerializationConverter` instance with the `Json` parameter and assign this instance to the `contentConverter` property:

```kotlin
import io.ktor.server.plugins.*
import io.ktor.serialization.kotlinx.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(Json)
}
```

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer in your application, assign `GsonWebsocketContentConverter` to the `contentConverter` property:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.serializaion.gson.*

install(WebSockets) {
    contentConverter = GsonWebsocketContentConverter()
}
```


</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer in your application, assign `JacksonWebsocketContentConverter` to the `contentConverter` property:

```kotlin
import io.ktor.server.plugins.*
import io.ktor.serializaion.jackson.*

install(WebSockets) {
    contentConverter = JacksonWebsocketContentConverter()
}
```

</tab>
</tabs>



### XML serializer {id="register_xml"}

To register the XML serializer in your application, create a `KotlinxWebsocketSerializationConverter` instance with the `XML` parameter and assign this instance to the `contentConverter` property:
```kotlin
import io.ktor.server.plugins.*
import nl.adaptivity.xmlutil.serialization.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(XML)
}
```


### CBOR serializer {id="register_cbor"}
To register the CBOR serializer in your application, call the `cbor` method:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.serialization.kotlinx.cbor.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
}
```


## Receive and send data {id="receive_send_data"}

### Create a data class {id="create_data_class"}
To serialize/deserialize frames into/from an object, you need to create a data class, for example:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" lines="11"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" lines="10-11"}

### Receive data {id="receive_data"}
To receive and convert a content of a text frame, call the `receiveDeserialized` method that accepts a data class as a parameter:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" lines="23-26"}

You can find the full example here: [server-websockets-serialization](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets-serialization).

### Send data {id="send_data"}
To pass a data object in a text frame, you can use the `sendSerialized` method:

```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" lines="20-22"}



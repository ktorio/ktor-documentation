[//]: # (title: WebSockets serialization)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="server-websockets-serialization"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Similar to the [ContentNegotiation](server-serialization.md) plugin, WebSockets allow you to serialize/deserialize text frames in a specific format. Ktor supports the following formats out-of-the-box: JSON, XML, CBOR, and ProtoBuf.


## Add dependencies {id="add_dependencies"}

<include from="server-serialization.md" element-id="add_serialization_dependency"/>


## Configure a serializer {id="configure_serializer"}

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON serializer in the WebSockets [configuration](server-websockets.md#configure), create a `KotlinxWebsocketSerializationConverter` instance with the `Json` parameter and assign this instance to the `contentConverter` property:

```kotlin
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(Json)
}
```

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer, assign `GsonWebsocketContentConverter` to the `contentConverter` property:
```kotlin
import io.ktor.serialization.gson.*

install(WebSockets) {
    contentConverter = GsonWebsocketContentConverter()
}
```


</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer, assign `JacksonWebsocketContentConverter` to the `contentConverter` property:

```kotlin
import io.ktor.serialization.jackson.*

install(WebSockets) {
    contentConverter = JacksonWebsocketContentConverter()
}
```

</tab>
</tabs>



### XML serializer {id="register_xml"}

To register the XML serializer in the WebSockets [configuration](server-websockets.md#configure), create a `KotlinxWebsocketSerializationConverter` instance with the `XML` parameter and assign this instance to the `contentConverter` property:
```kotlin
import nl.adaptivity.xmlutil.serialization.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(XML)
}
```


### CBOR serializer {id="register_cbor"}
To register the CBOR serializer in the WebSockets [configuration](server-websockets.md#configure), create a `KotlinxWebsocketSerializationConverter` instance with the `Cbor` parameter and assign this instance to the `contentConverter` property:

```kotlin
import io.ktor.serialization.kotlinx.cbor.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
}
```

### ProtoBuf serializer {id="register_protobuf"}
To register the ProtoBuf serializer in the WebSockets [configuration](server-websockets.md#configure), create a `KotlinxWebsocketSerializationConverter` instance with the `ProtoBuf` parameter and assign this instance to the `contentConverter` property:

```kotlin
import kotlinx.serialization.protobuf.*

install(WebSockets) {
    contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
}
```


## Receive and send data {id="receive_send_data"}

### Create a data class {id="create_data_class"}
To serialize/deserialize frames into/from an object, you need to create a data class, for example:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" include-lines="11"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" include-lines="10-11"}

### Receive data {id="receive_data"}
To receive and convert a content of a text frame, call the `receiveDeserialized` function that accepts a data class as a parameter:
```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" include-lines="23-26"}

To receive deserialized frames from the [incoming](server-websockets.md#api-overview) channel, use the [WebsocketContentConverter.deserialize](https://api.ktor.io/ktor-shared/ktor-serialization/io.ktor.serialization/-websocket-content-converter/deserialize.html) function. `WebsocketContentConverter` is available via the `WebSocketServerSession.converter` property.

### Send data {id="send_data"}
To pass a data object in a text frame using a [specified format](#configure_serializer), you can use the `sendSerialized` function:

```kotlin
```
{src="snippets/server-websockets-serialization/src/main/kotlin/com/example/Application.kt" include-lines="20-22"}

> You can find the full example here: [server-websockets-serialization](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/server-websockets-serialization).

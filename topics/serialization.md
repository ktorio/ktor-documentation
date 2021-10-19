[//]: # (title: Content negotiation and serialization)

<var name="plugin_name" value="ContentNegotiation"/>
<var name="artifact_name" value="ktor-server-content-negotiation"/>

<microformat>
<var name="example_name" value="json-kotlinx"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>


The [ContentNegotiation](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-content-negotiation/index.html) plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in the specific format. Ktor supports two formats out-of-the-box: JSON and XML.


## Add dependencies {id="add_dependencies"}

### ContentNegotiation {id="add_content_negotiation_dependency"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

Note that serializers for specific formats require additional artifacts. For example, kotlinx.serialization requires the `ktor-shared-serialization-kotlinx-json` dependency for JSON.

### JSON {id="add_json_dependency"}

To serialize/deserialize JSON data, you can choose one of the following libraries: kotlinx.serialization, Gson, or Jackson. 

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

1. Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.
2. Add the `ktor-shared-serialization-kotlinx-json` artifact in the build script:
   <var name="artifact_name" value="ktor-shared-serialization-kotlinx-json"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>

</tab>
<tab title="Gson" group-key="gson">

* Add the `ktor-shared-serialization-gson` artifact in the build script:
   <var name="artifact_name" value="ktor-shared-serialization-gson"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>

</tab>
<tab title="Jackson" group-key="jackson">

* Add the `ktor-shared-serialization-jackson` artifact in the build script:
   <var name="artifact_name" value="ktor-shared-serialization-jackson"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>

</tab>
</tabs>


### XML {id="add_xml_dependency"}

To serialize/deserialize XML, add the `ktor-shared-serialization-kotlinx-xml` in the build script:
<var name="artifact_name" value="ktor-shared-serialization-kotlinx-xml"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install ContentNegotiation {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure a serializer {id="configure_serializer"}

Ktor supports two formats out-of-the-box: [JSON](#register_json) and [XML](#register_xml). You can also register any converter supported by kotlinx.serialization or implement a custom converter.

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON converter in your application, call the `json` method:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.shared.serialization.kotlinx.*

install(ContentNegotiation) {
    json()
}
```

The `json` method also allows you to adjust serialization settings provided by [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html), for example:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="25-30"}

</tab>
<tab title="Gson" group-key="gson">

To register the Gson converter in your application, call the [gson](https://api.ktor.io/ktor-features/ktor-gson/ktor-gson/io.ktor.gson/gson.html) method:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.shared.serializaion.gson.*

install(ContentNegotiation) {
    gson()
}
```

The `gson` method also allows you to adjust serialization settings provided by [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html), for example:

```kotlin
```
{src="snippets/gson/src/GsonApplication.kt" lines="21-26"}

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson converter in your application, call the [jackson](https://api.ktor.io/ktor-features/ktor-jackson/ktor-jackson/io.ktor.jackson/jackson.html) method:

```kotlin
import io.ktor.server.plugins.*
import io.ktor.shared.serializaion.jackson.*

install(ContentNegotiation) {
    jackson()
}
```

The `jackson` method also allows you to adjust serialization settings provided by [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html), for example:

```kotlin
```
{src="snippets/jackson/src/JacksonApplication.kt" lines="23-32"}

</tab>
</tabs>



### XML serializer {id="register_xml"}

To register the XML converter in your application, call the `xml` method:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.shared.serialization.kotlinx.xml.*

install(ContentNegotiation) {
    xml()
}
```

The `xml` method also allows you to access XML serialization settings, for example:

```kotlin
import io.ktor.server.plugins.*
import io.ktor.shared.serialization.kotlinx.xml.*
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.serialization.*

install(ContentNegotiation) {
    xml(format = XML {
        xmlDeclMode = XmlDeclMode.Charset
    })
}
```

### Arbitrary kotlinx.serialization serializer {id="register_arbitrary_converter"}

To register an arbitrary serializer from the kotlinx.serialization library (such as Protobuf or CBOR), call the [serialization](https://api.ktor.io/ktor-features/ktor-serialization/ktor-serialization/io.ktor.serialization/serialization.html) method and pass two parameters:
* The required [ContentType](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http/-content-type/index.html) value.
* An object of the class implementing the required encoder/decoder.

For example, you can register the [Cbor](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor/index.html) converter in the following way:
```kotlin
install(ContentNegotiation) {
    serialization(ContentType.Application.Cbor, Cbor.Default)
}
```

### Custom serializer {id="register_custom"}

To register a custom serializer for a specified `Content-Type`, you need to call the [register](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-content-negotiation/-configuration/register.html) method. In the example below, two [custom serializer](#implement_custom_converter) are registered to deserialize `application/json` and `application/xml` data:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, CustomJsonConverter())
    register(ContentType.Application.Xml, CustomXmlConverter())
}
```


## Receive and send data {id="receive_send_data"}

### Create a data class {id="create_data_class"}
To deserialize received data into an object, you need to create a data class, for example:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="14"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="10,12-14"}

### Receive data {id="receive_data"}
To receive and convert a content for a request, call the [receive](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.request/receive.html) method that accepts a data class as a parameter:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="38-42"}

The `Content-Type` of the request will be used to choose a [converter](#register_converter) for processing the request. The example below shows a sample [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) request containing JSON or XML data that is converted to a `Customer` object on the server side:

<tabs>
<tab title="JSON">

```HTTP
```
{src="snippets/json-kotlinx/post.http" lines="1-9"}

</tab>
<tab title="XML">

```HTTP
```
{src="snippets/json-kotlinx/post.http" lines="12-15"}

</tab>
</tabs>



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



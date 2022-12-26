[//]: # (title: Content negotiation and serialization)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="ContentNegotiation"/>
<var name="package_name" value="io.ktor.server.plugins.contentnegotiation"/>
<var name="artifact_name" value="ktor-server-content-negotiation"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="json-kotlinx"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
The ContentNegotiation plugin serves two primary purposes: negotiating media types between the client and server and serializing/deserializing the content in a specific format.
</link-summary>


The [ContentNegotiation](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-content-negotiation/io.ktor.server.plugins.contentnegotiation/-content-negotiation.html) plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in a specific format. Ktor supports the following formats out-of-the-box: JSON, XML, CBOR, and ProtoBuf.


## Add dependencies {id="add_dependencies"}

### ContentNegotiation {id="add_content_negotiation_dependency"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

Note that serializers for specific formats require additional artifacts. For example, kotlinx.serialization requires the `ktor-serialization-kotlinx-json` dependency for JSON.


### Serialization {id="serialization_dependency"}

<snippet id="add_serialization_dependency">

Before using kotlinx.serialization converters, you need to add the Kotlin serialization plugin 
as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

#### JSON {id="add_json_dependency"}

To serialize/deserialize JSON data, you can choose one of the following libraries: kotlinx.serialization, Gson, or Jackson. 

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

Add the `ktor-serialization-kotlinx-json` artifact in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-json"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
<tab title="Gson" group-key="gson">

Add the `ktor-serialization-gson` artifact in the build script:

<var name="artifact_name" value="ktor-serialization-gson"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
<tab title="Jackson" group-key="jackson">

Add the `ktor-serialization-jackson` artifact in the build script:

<var name="artifact_name" value="ktor-serialization-jackson"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
</tabs>


#### XML {id="add_xml_dependency"}

To serialize/deserialize XML, add the `ktor-serialization-kotlinx-xml` in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-xml"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

Note that XML serialization is supported on JVM only.

#### CBOR {id="add_cbor_dependency"}

To serialize/deserialize CBOR, add the `ktor-serialization-kotlinx-cbor` in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-cbor"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

#### ProtoBuf {id="add_protobuf_dependency"}

To serialize/deserialize ProtoBuf, add the `ktor-serialization-kotlinx-protobuf` in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-protobuf"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</snippet>


## Install ContentNegotiation {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure a serializer {id="configure_serializer"}

Ktor supports the following formats out-of-the-box: [JSON](#register_json), [XML](#register_xml), [CBOR](#register_cbor). You can also implement your own custom serializer.

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON serializer in your application, call the `json` method:
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

install(ContentNegotiation) {
    json()
}
```

The `json` method also allows you to adjust serialization settings provided by [JsonBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/), for example:

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="25-30"}

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer in your application, call the `gson` method:
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

install(ContentNegotiation) {
    gson()
}
```

The `gson` method also allows you to adjust serialization settings provided by [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html), for example:

```kotlin
```
{src="snippets/gson/src/main/kotlin/com/example/GsonApplication.kt" include-lines="24-29"}

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer in your application, call the `jackson` method:

```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

install(ContentNegotiation) {
    jackson()
}
```

The `jackson` method also allows you to adjust serialization settings provided by [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html), for example:

```kotlin
```
{src="snippets/jackson/src/main/kotlin/com/example/JacksonApplication.kt" include-lines="26-35"}

</tab>
</tabs>



### XML serializer {id="register_xml"}

To register the XML serializer in your application, call the `xml` method:
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.xml.*

install(ContentNegotiation) {
    xml()
}
```

The `xml` method also allows you to access XML serialization settings, for example:

```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.xml.*
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.serialization.*

install(ContentNegotiation) {
    xml(format = XML {
        xmlDeclMode = XmlDeclMode.Charset
    })
}
```


### CBOR serializer {id="register_cbor"}
To register the CBOR serializer in your application, call the `cbor` method:
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*

install(ContentNegotiation) {
    cbor()
}
```

The `cbor` method also allows you to access CBOR serialization settings provided by [CborBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor-builder/), for example:

```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*
import kotlinx.serialization.cbor.*

install(ContentNegotiation) {
    cbor(Cbor {
        ignoreUnknownKeys = true
    })
}
```

### ProtoBuf serializer {id="register_protobuf"}
To register the ProtoBuf serializer in your application, call the `protobuf` method:
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.protobuf.*

install(ContentNegotiation) {
    protobuf()
}
```

The `protobuf` method also allows you to access ProtoBuf serialization settings provided by [ProtoBufBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-protobuf/kotlinx.serialization.protobuf/-proto-buf-builder/), for example:

```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.protobuf.*
import kotlinx.serialization.protobuf.*

install(ContentNegotiation) {
    protobuf(ProtoBuf {
        encodeDefaults = true
    })
}
```



### Custom serializer {id="register_custom"}

To register a custom serializer for a specified `Content-Type`, you need to call the `register` method. In the example below, two [custom serializer](#implement_custom_serializer) are registered to deserialize `application/json` and `application/xml` data:

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
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="14"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="10,12-14"}

### Receive data {id="receive_data"}
To receive and convert a content for a request, call the `receive` method that accepts a data class as a parameter:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="38-42"}

The `Content-Type` of the request will be used to choose a [serializer](#configure_serializer) for processing the request. The example below shows a sample [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) request containing JSON or XML data that is converted to a `Customer` object on the server side:

<tabs>
<tab title="JSON">

```HTTP
```
{src="snippets/json-kotlinx/post.http" include-lines="1-9"}

</tab>
<tab title="XML">

```HTTP
```
{src="snippets/json-kotlinx/post.http" include-lines="12-15"}

</tab>
</tabs>



You can find the full example here: [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).

### Send data {id="send_data"}
To pass a data object in a response, you can use the `respond` method:
```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="32-36"}

In this case, Ktor uses the `Accept` header to choose the required [serializer](#configure_serializer). You can find the full example here: [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).



## Implement a custom serializer {id="implement_custom_serializer"}

In Ktor, you can write your own [serializer](#configure_serializer) for serializing/deserializing data. To do this, you need to implement the [ContentConverter](https://api.ktor.io/ktor-shared/ktor-serialization/io.ktor.serialization/-content-converter/index.html) interface:
```kotlin
interface ContentConverter {
    suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any): OutgoingContent?
    suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any?
}
```
Take a look at the [GsonConverter](https://github.com/ktorio/ktor/blob/main/ktor-shared/ktor-serialization/ktor-serialization-gson/jvm/src/GsonConverter.kt) class as an implementation example.  



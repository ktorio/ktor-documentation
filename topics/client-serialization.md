[//]: # (title: Content negotiation and serialization in Ktor Client)

<show-structure for="chapter" depth="2"/>
<primary-label ref="client-plugin"/>

<var name="plugin_name" value="ContentNegotiation"/>
<var name="artifact_name" value="ktor-client-content-negotiation"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-json-kotlinx"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The ContentNegotiation plugin serves two primary purposes: negotiating media types between the client and server and serializing/deserializing the content in a specific format when sending requests and receiving responses.
</link-summary>

The [ContentNegotiation](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-content-negotiation/io.ktor.client.plugins.contentnegotiation/-content-negotiation)
plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in a specific format when sending [requests](client-requests.md) and receiving [responses](client-responses.md). Ktor supports the following formats out-of-the-box: JSON, XML, CBOR, and ProtoBuf.

> On the server, Ktor provides the [ContentNegotiation](server-serialization.md) plugin for serializing/deserializing content.


## Add dependencies {id="add_dependencies"}
### ContentNegotiation {id="add_content_negotiation_dependency"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

Note that serializers for specific formats require additional artifacts. For example, kotlinx.serialization requires the `ktor-serialization-kotlinx-json` dependency for JSON. Depending on the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer) explicitly and configure it.

### Serialization {id="serialization_dependency"}

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

> Note that XML serialization [is not supported on `jsNode` target](https://github.com/pdvrieze/xmlutil/issues/83).
{style="note"}

#### CBOR {id="add_cbor_dependency"}

To serialize/deserialize CBOR, add the `ktor-serialization-kotlinx-cbor` in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-cbor"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

#### ProtoBuf {id="add_protobuf_dependency"}

To serialize/deserialize ProtoBuf, add the `ktor-serialization-kotlinx-protobuf` in the build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-protobuf"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install ContentNegotiation {id="install_plugin"}

To install `ContentNegotiation`, pass it to the `install` function inside a [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation)
}
```
Now you can [configure](#configure_serializer) the required JSON serializer.


## Configure a serializer {id="configure_serializer"}

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON serializer in your application, call the `json` method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}
```

In the `json` constructor, you can access the [JsonBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/) API, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="24-31"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-json-kotlinx).

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer in your application, call the [gson](https://api.ktor.io/ktor-shared/ktor-serialization/ktor-serialization-gson/io.ktor.serialization.gson/gson.html) method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        gson()
    }
}
```

The `gson` method also allows you to adjust serialization settings provided by [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html).

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer in your application, call the [jackson](https://api.ktor.io/ktor-shared/ktor-serialization/ktor-serialization-jackson/io.ktor.serialization.jackson/jackson.html) method:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson()
    }
}
```

The `jackson` method also allows you to adjust serialization settings provided by [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html).

</tab>
</tabs>

### XML serializer {id="register_xml"}

To register the XML serializer in your application, call the `xml` method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.xml.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        xml()
    }
}
```

The `xml` method also allows you to access XML serialization settings, for example:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.xml.*
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.serialization.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        xml(format = XML {
            xmlDeclMode = XmlDeclMode.Charset
        })
    }
}
```

### CBOR serializer {id="register_cbor"}
To register the CBOR serializer in your application, call the `cbor` method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        cbor()
    }
}
```

The `cbor` method also allows you to access CBOR serialization settings provided by [CborBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor-builder/), for example:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*
import kotlinx.serialization.cbor.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        cbor(Cbor {
            ignoreUnknownKeys = true
        })
    }
}
```

### ProtoBuf serializer {id="register_protobuf"}
To register the ProtoBuf serializer in your application, call the `protobuf` method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.protobuf.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        protobuf()
    }
}
```

The `protobuf` method also allows you to access ProtoBuf serialization settings provided by [ProtoBufBuilder](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-protobuf/kotlinx.serialization.protobuf/-proto-buf-builder/), for example:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.protobuf.*
import kotlinx.serialization.protobuf.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        protobuf(ProtoBuf {
            encodeDefaults = true
        })
    }
}
```



## Receive and send data {id="receive_send_data"}
### Create a data class {id="create_data_class"}

To receive and send data, you need to have a data class, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="19"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="18-19"}

<include from="server-serialization.md" element-id="serialization_types"/>

### Send data {id="send_data"}

To send a [class instance](#create_data_class) within a [request](client-requests.md) body as JSON, assign this instance using `setBody` function and set the content type to `application/json` by calling `contentType`:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="33-36"}

To send data as XML or CBOR, set `contentType` to `ContentType.Application.Xml` or `ContentType.Application.Cbor`, respectively.

### Receive data {id="receive_data"}

When a server sends a [response](client-responses.md) with the `application/json`, `application/xml`, or `application/cbor` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of a function used to receive a response payload (`body` in the example below):
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="39"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-json-kotlinx).

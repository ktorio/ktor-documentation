[//]: # (title: Content negotiation and serialization)

<var name="plugin_name" value="ContentNegotiation"/>
<var name="artifact_name" value="ktor-client-content-negotiation"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-json-kotlinx"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
The ContentNegotiation plugin serves two primary purposes: negotiating media types between the client and server and serializing/deserializing the content in a specific format when sending requests and receiving responses.
</excerpt>

The `ContentNegotiation` plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in a specific format when sending [requests](request.md) and receiving [responses](response.md). Ktor supports the following formats out-of-the-box: JSON, XML, and CBOR.

> On the server, Ktor provides the [ContentNegotiation](serialization.md) plugin for serializing/deserializing content.


## Add dependencies {id="add_dependencies"}
### ContentNegotiation {id="add_content_negotiation_dependency"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

Note that serializers for specific formats require additional artifacts. For example, kotlinx.serialization requires the `ktor-serialization-kotlinx-json` dependency for JSON. Depending on the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer) explicitly and configure it.

<include src="serialization.md" include-id="serialization_dependency"/>
      

## Install ContentNegotiation {id="install_plugin"}
To install `ContentNegotiation`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
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

install(ContentNegotiation) {
    json()
}
```

In the `json` constructor, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="21-26"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-json-kotlinx).

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer in your application, call the [gson](https://api.ktor.io/ktor-shared/ktor-serialization/ktor-serialization-gson/io.ktor.serialization.gson/gson.html) method:
```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

install(ContentNegotiation) {
    gson()
}
```

The `gson` method also allows you to adjust serialization settings provided by [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html).

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer in your application, call the [jackson](https://api.ktor.io/ktor-shared/ktor-serialization/ktor-serialization-jackson/io.ktor.serialization.jackson/jackson.html) method:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

install(ContentNegotiation) {
    jackson()
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

install(ContentNegotiation) {
    xml()
}
```

The `xml` method also allows you to access XML serialization settings, for example:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
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
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*

install(ContentNegotiation) {
    cbor()
}
```

The `cbor` method also allows you to access CBOR serialization settings provided by [CborBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor-builder/index.html), for example:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*
import kotlinx.serialization.cbor.*

install(ContentNegotiation) {
    cbor(Cbor {
        ignoreUnknownKeys = true
    })
}
```


## Receive and send data {id="receive_send_data"}
### Create a data class {id="create_data_class"}

To receive and send data, you need to have a data class, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="16"}

If you use kotlinx.serialization, make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="15-16"}

To learn more about `kotlinx.serialization`, see the [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

### Send data {id="send_data"}

To send a [class instance](#create_data_class) within a [request](request.md) body as JSON, assign this instance using `setBody` function and set the content type to `application/json` by calling `contentType`:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="29-32"}

To send data as XML or CBOR, set `contentType` to `ContentType.Application.Xml` or `ContentType.Application.Cbor`, respectively.

### Receive data {id="receive_data"}

When a server sends a [response](response.md) with the `application/json`, `application/xml`, or `application/cbor` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of a function used to receive a response payload (`body` in the example below):
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="35"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-json-kotlinx).

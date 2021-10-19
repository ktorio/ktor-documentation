[//]: # (title: Content negotiation and serialization)

<var name="plugin_name" value="ContentNegotiation"/>
<var name="artifact_name" value="ktor-client-content-negotiation"/>

<microformat>
<var name="example_name" value="client-json-kotlinx"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `ContentNegotiation` plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing JSON data when sending [requests](request.md) and receiving [responses](response.md). Ktor supports two formats out-of-the-box: JSON and XML. This functionality is provided for [Kotlin Multiplatform](http-client_multiplatform.md) by using kotlinx.serialization and for JVM by using the `Gson`/`Jackson` libraries.

> On the server, Ktor provides the [ContentNegotiation](serialization.md) plugin for serializing/deserializing content.


## Add dependencies {id="add_dependencies"}
### ContentNegotiation {id="add_content_negotiation_dependency"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

Note that serializers for specific formats require additional artifacts. For example, kotlinx.serialization requires the `ktor-shared-serialization-kotlinx-json` dependency for JSON. Depending on the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer) explicitly and configure it.

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
To install `ContentNegotiation`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
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

To register the JSON converter in your application, call the `json` method:
```kotlin
import io.ktor.client.plugins.*
import io.ktor.shared.serialization.kotlinx.*

install(ContentNegotiation) {
    json()
}
```

In the `json` constructor, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="21-26"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).

</tab>
<tab title="Gson" group-key="gson">

To register the Gson converter in your application, call the [gson](https://api.ktor.io/ktor-features/ktor-gson/ktor-gson/io.ktor.gson/gson.html) method:
```kotlin
import io.ktor.client.plugins.*
import io.ktor.shared.serializaion.gson.*

install(ContentNegotiation) {
    gson()
}
```

The `gson` method also allows you to adjust serialization settings provided by [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html).

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson converter in your application, call the [jackson](https://api.ktor.io/ktor-features/ktor-jackson/ktor-jackson/io.ktor.jackson/jackson.html) method:

```kotlin
import io.ktor.client.plugins.*
import io.ktor.shared.serializaion.jackson.*

install(ContentNegotiation) {
    jackson()
}
```

The `jackson` method also allows you to adjust serialization settings provided by [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html).

</tab>
</tabs>

### XML serializer {id="register_xml"}

To register the XML converter in your application, call the `xml` method:
```kotlin
import io.ktor.client.plugins.*
import io.ktor.shared.serialization.kotlinx.xml.*

install(ContentNegotiation) {
    xml()
}
```

The `xml` method also allows you to access XML serialization settings, for example:

```kotlin
import io.ktor.client.plugins.*
import io.ktor.shared.serialization.kotlinx.xml.*
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.serialization.*

install(ContentNegotiation) {
    xml(format = XML {
        xmlDeclMode = XmlDeclMode.Charset
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

To send data as XML, set `contentType` to `ContentType.Application.Xml`.

### Receive data {id="receive_data"}

When a server sends a [response](response.md) with the `application/json` or `application/xml` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of a function used to receive a response payload (`body` in the example below):
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="35"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).

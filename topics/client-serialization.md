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

The [`ContentNegotiation`](https://api.ktor.io/ktor-client-content-negotiation/io.ktor.client.plugins.contentnegotiation/-content-negotiation)
plugin serves two primary purposes:
* Negotiating media types between the client and server, using the `Accept` and `Content-Type` headers.
* Serializing [request](client-requests.md) bodies and deserializing [response](client-responses.md) bodies in supported
  formats. Ktor provides built-in support for JSON, XML, CBOR, and ProtoBuf.

> On the server, Ktor provides the [`ContentNegotiation`](server-serialization.md) plugin for serializing and deserializing
> content.
>
{style="tip"}

## Add dependencies {id="add_dependencies"}

### Content negotiation {id="add_content_negotiation_dependency"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

> Serializers for specific formats require additional artifacts.
> 
> For example, `kotlinx.serialization` requires the `ktor-serialization-kotlinx-json` dependency for JSON. Depending on
> the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer)
> explicitly and configure it.
> 
{style="note"}

<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

### Serialization {id="serialization_dependency"}

Before using `kotlinx.serialization` converters, add the Kotlin serialization plugin as described in the
[Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

#### JSON {id="add_json_dependency"}

To serialize and deserialize JSON data, add a serialization library to your project. Ktor supports
`kotlinx.serialization`, Gson, or Jackson.

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

Add the `ktor-serialization-kotlinx-json` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-json"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
<tab title="Gson" group-key="gson">

Add the `ktor-serialization-gson` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-gson"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
<tab title="Jackson" group-key="jackson">

Add the `ktor-serialization-jackson` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-jackson"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

</tab>
</tabs>

#### XML {id="add_xml_dependency"}

To serialize and deserialize XML, add the `ktor-serialization-kotlinx-xml` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-xml"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

#### CBOR {id="add_cbor_dependency"}

To serialize and deserialize CBOR, add the `ktor-serialization-kotlinx-cbor` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-cbor"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

#### ProtoBuf {id="add_protobuf_dependency"}

To serialize and deserialize ProtoBuf, add the `ktor-serialization-kotlinx-protobuf` artifact in your build script:

<var name="artifact_name" value="ktor-serialization-kotlinx-protobuf"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install `ContentNegotiation` {id="install_plugin"}

To install the `ContentNegotiation` plugin, pass it to the `install` function in the [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation)
}
```

You can then [configure](#configure_serializer) the required JSON serializer.

## Configure a serializer {id="configure_serializer"}

### JSON serializer {id="register_json"}

<tabs group="json-libraries">
<tab title="kotlinx.serialization" group-key="kotlinx">

To register the JSON serializer in your application, call the `json()` function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}
```

To customize JSON serialization, pass a `Json` configuration in the `json()` constructor:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="24-31"}

For available configuration options, see [`JsonBuilder`](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/).

> For the full example, see [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).
>
{style="tip"}

</tab>
<tab title="Gson" group-key="gson">

To register the Gson serializer in your application, call the [`gson()`](https://api.ktor.io/ktor-serialization-gson/io.ktor.serialization.gson/gson.html) function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        gson()
    }
}
```

To customize Gson serialization, pass a `Json` configuration in the `gson()` constructor. For available configuration
options, see [`GsonBuilder`](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html).

</tab>
<tab title="Jackson" group-key="jackson">

To register the Jackson serializer in your application, call the [`jackson()`](https://api.ktor.io/ktor-serialization-jackson/io.ktor.serialization.jackson/jackson.html) function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson()
    }
}
```

To customize Jackson serialization, use the settings provided by [`ObjectMapper`](https://fasterxml.github.io/jackson-databind/javadoc/2.17.2/com/fasterxml/jackson/databind/ObjectMapper.html):

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.*
import java.text.DateFormat

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
        }
    }
}
```

</tab>
</tabs>

### XML serializer {id="register_xml"}

To register the XML serializer in your application, call the `xml()` function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.xml.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        xml()
    }
}
```

To customize XML serialization, pass the required options to the `xml()` function:

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

To register the CBOR serializer in your application, call the `cbor()` function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.cbor.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        cbor()
    }
}
```

To customize CBOR serialization, pass a `Cbor` configuration in the `cbor()` constructor:

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

For available configuration
options, see [`CborBuilder`](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor-builder/)

### ProtoBuf serializer {id="register_protobuf"}

To register the ProtoBuf serializer in your application, call the `protobuf()` function:

```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.protobuf.*

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        protobuf()
    }
}
```

To customize ProtoBuf serialization, pass a `ProtoBuf` configuration to the `protobuf()` function:

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

For available options, see [`ProtoBufBuilder`](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-protobuf/kotlinx.serialization.protobuf/-proto-buf-builder/).

## Configure the `Accept` header {id="configure_accept_header"}

By default, the `ContentNegotiation` plugin adds registered content types to the `Accept` header of outgoing requests.

If you set an `Accept` header explicitly and don't want the plugin to add registered content types, set
the `acceptHeaderMergeStrategy` property to `ContentTypeMergeStrategy.SkipIfPresent`:

```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, noOpJsonConverter)
        acceptHeaderMergeStrategy = ContentTypeMergeStrategy.SkipIfPresent
    }
}
```

With `SkipIfPresent`, the plugin preserves an existing `Accept` header. If the request doesn't contain an `Accept` header,
the plugin adds the registered content types as usual.

## Send and receive data {id="receive_send_data"}

### Create a data class {id="create_data_class"}

The following examples use a `Customer` data class to represent the data sent and received by the client:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="19"}

If you use `kotlinx.serialization`, annotate the class with `@Serializable`:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="18-19"}

<include from="server-serialization.md" element-id="serialization_types"/>

### Send data {id="send_data"}

To send a [class instance](#create_data_class) in a [request](client-requests.md) body, assign this instance using the `setBody()`
function and set the content type using the `contentType()` function.

The following example sends a `Customer` object as JSON:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="33-36"}

The `ContentNegotiation` plugin uses the configured serializer to convert the request body to the specified format.

To send data in another registered format, specify the corresponding content type, such as `ContentType.Application.Xml`
or `ContentType.Application.Cbor`.

### Receive data {id="receive_data"}

When the server returns a [response](client-responses.md) with a supported content type, the `ContentNegotiation` plugin can deserialize
the response body into the expected type.

For example, to deserialize a JSON response into a `Customer` object, call the `body()` function:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="39"}

> For the full example, see [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).
>
{style="tip"}
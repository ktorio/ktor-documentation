[//]: # (title: Content negotiation and serialization)

<microformat>
<var name="example_name" value="client-json-kotlinx"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `ContentNegotiation` plugin serves two primary purposes:
* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing JSON data when sending [requests](request.md) and receiving [responses](response.md). This functionality is provided for [Kotlin Multiplatform](http-client_multiplatform.md) by using `kotlinx.serialization` and for JVM by using the `Gson`/`Jackson` libraries.

> On the server, Ktor provides the [ContentNegotiation](serialization.md) plugin for serializing/deserializing content.


## Add dependencies {id="add_dependencies"}
### Add ContentNegotiation dependency {id="add_content_negotiation_dependency"}
Before installing `ContentNegotiation`, you need to add the `ktor-client-content-negotiation` artifact in the build script:
<var name="artifact_name" value="ktor-client-content-negotiation"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

Then, you need to add a dependency for the desired serializer. For [multiplatform](http-client_multiplatform.md) projects, use the [kotlinx.serialization](#kotlinx_dependency) library. If your project targets only JVM, you can add [Gson or Jackson](#jvm_dependency) dependency. Depending on the included artifacts, Ktor chooses a default serializer automatically. If required, you can [specify the serializer](#configure_serializer) explicitly and configure it.


### Multiplatform: kotlinx {id="kotlinx_dependency"}

For [multiplatform](http-client_multiplatform.md) projects, you can use the `kotlinx.serialization` library. You can add it to the project as follows:
1. Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.
2. Add the `ktor-shared-serialization-kotlinx` artifact:
   <var name="artifact_name" value="ktor-shared-serialization-kotlinx"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>


### JVM: Gson and Jackson  {id="jvm_dependency"}
To use Gson, add the following artifact to the build script:
<var name="artifact_name" value="ktor-shared-serialization-gson"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

For Jackson, add the following dependency:
<var name="artifact_name" value="ktor-shared-serialization-jackson"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>
      

## Install ContentNegotiation {id="install_feature"}
To install `ContentNegotiation`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation)
}
```
Now you can [configure](#configure_serializer) the required JSON serializer.


## Configure a serializer {id="configure_serializer"}
### kotlinx {id="kotlinx"}

To use the `kotlinx.serialization` library to serialize/deserialize JSON data, call the `json` function:
```kotlin
install(ContentNegotiation) {
   json()
}
```
Inside the `KotlinxSerializer` constructor, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="21-26"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).


### Gson {id="gson"}

To use Gson, call the `gson` function:
```kotlin
install(ContentNegotiation) {
   gson()
}
```
Inside the `gson` block parameter, you can access [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html) API, for example: 
```kotlin
install(ContentNegotiation) {
    gson() {
        setPrettyPrinting()
        disableHtmlEscaping()
    }
}
```

### Jackson {id="jackson"}

To use Gson, call the `jackson` function:
```kotlin
install(ContentNegotiation) {
   jackson()
}
```
Inside the `jackson` block parameter, you can access the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html) API:
```kotlin
install(ContentNegotiation) {
   jackson() {
        enable(SerializationFeature.INDENT_OUTPUT)
        dateFormat = DateFormat.getDateInstance()
    }
}
```


## Receive and send data {id="receive_send_data"}
### Create a data class {id="create_data_class"}

To receive and send data, you need to have a data class, for example:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="16"}

If you use [kotlinx.serialization](#kotlinx), make sure that this class has the `@Serializable` annotation:
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="15-16"}

To learn more about `kotlinx.serialization`, see the [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

### Send data {id="send_data"}

To send a [class instance](#create_data_class) within a [request](request.md) body as JSON, assign this instance using `setBody` function and set the content type to `application/json` by calling `contentType`:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="29-32"}

### Receive data {id="receive_data"}

When a server sends a [response](response.md) with the `application/json` content, you can deserialize it by specifying a [data class](#create_data_class) as a parameter of a function used to receive a response payload (`body` in the example below):
```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="35"}

You can find the full example here: [client-json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-json-kotlinx).

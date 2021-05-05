[//]: # (title: Jackson)

<microformat>
<var name="example_name" value="jackson"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

ContentNegotiation for [server](serialization-server.md) and [client](serialization-client.md) provides the built-in [Jackson](https://github.com/FasterXML/jackson) converter for handing JSON data in your application. 

## Add dependencies {id="add_dependencies"}
Before registering the Jackson converter, you need to include the following artifacts in the build script:
<var name="artifact_name" value="ktor-shared-serialization-jackson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Register the Jackson converter {id="register_jackson_converter"}
To register the Jackson converter in your application, call the [jackson](https://api.ktor.io/%ktor_version%/io.ktor.jackson/jackson.html) method:
```kotlin
import io.ktor.shared.serializaion.jackson.*

install(ContentNegotiation) {
    jackson()
}
```
Inside the `jackson` block, you can access the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html) API, for example:
```kotlin
install(ContentNegotiation) {
    jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        dateFormat = DateFormat.getDateInstance()
        // ...
    }
}
```
To learn how to receive and send data, see [server](serialization-server.md#receive_send_data) or [client](serialization-client.md#receive_send_data) docs.

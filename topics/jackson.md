[//]: # (title: Jackson)

<var name="artifact_name" value="ktor-serialization-jackson"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="jackson"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

[ContentNegotiation](serialization.md) provides the built-in [Jackson](https://github.com/FasterXML/jackson) converter for handing JSON data in your application. 

## Add dependencies {id="add_dependencies"}
Before registering the Jackson converter, you need to include the following artifacts in the build script:
* Add the `ktor-server-content-negotiation` artifact as described in [](serialization.md#add_dependencies).
* Add the `%artifact_name%` artifact:
  <var name="artifact_name" value="ktor-serialization-jackson"/>
  <include src="lib.xml" include-id="add_ktor_artifact"/>


## Register the Jackson converter {id="register_jackson_converter"}
To register the Jackson converter in your application, call the [jackson](https://api.ktor.io/ktor-features/ktor-jackson/ktor-jackson/io.ktor.jackson/jackson.html) method:
```kotlin
import io.ktor.server.plugins.*
import io.ktor.serialization.jackson.*

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
To learn how to receive and send data, see [](serialization.md#receive_send_data).

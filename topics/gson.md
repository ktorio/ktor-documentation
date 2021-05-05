[//]: # (title: Gson)

<microformat>
<var name="example_name" value="gson"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

ContentNegotiation for [server](serialization-server.md) and [client](serialization-client.md) provides the built-in [Gson](https://github.com/google/gson) converter for handing JSON data in your application.


## Add dependencies {id="add_dependencies"}
Before registering the Gson converter, you need to include the following artifacts in the build script:
<var name="artifact_name" value="ktor-shared-serialization-gson"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Register the Gson converter {id="register_gson_converter"}
To register the Gson converter in your application, call the [gson](https://api.ktor.io/%ktor_version%/io.ktor.gson/gson.html) method:
```kotlin
import io.ktor.shared.serializaion.gson.*

install(ContentNegotiation) {
    gson()
}
```
Inside the `gson` block, you can access the [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html) API, for example:
```kotlin
install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            // ...
        }
}
```
To learn how to receive and send data, see [server](serialization-server.md#receive_send_data) or [client](serialization-client.md#receive_send_data) docs.

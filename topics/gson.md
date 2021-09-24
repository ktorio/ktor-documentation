[//]: # (title: Gson)

<var name="artifact_name" value="ktor-shared-serialization-gson"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="gson"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

[ContentNegotiation](serialization.md) provides the built-in [Gson](https://github.com/google/gson) converter for handing JSON data in your application.


## Add dependencies {id="add_dependencies"}
Before registering the Gson converter, you need to include the following artifacts in the build script:
* Add the `ktor-server-content-negotiation` artifact as described in [](serialization.md#add_dependencies).
* Add the `%artifact_name%` artifact:
   <var name="artifact_name" value="ktor-shared-serialization-gson"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>


## Register the Gson converter {id="register_gson_converter"}
To register the Gson converter in your application, call the [gson](https://api.ktor.io/ktor-features/ktor-gson/ktor-gson/io.ktor.gson/gson.html) function:
```kotlin
import io.ktor.server.plugins.*
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
To learn how to receive and send data, see [](serialization.md#receive_send_data).
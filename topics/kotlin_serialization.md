[//]: # (title: kotlinx.serialization)

[ContentNegotiation](serialization.md) includes the built-in JSON converter provided by the [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) library.


## Add Dependencies {id="add_dependencies"}
Before registering the JSON converter, perform the following steps:

* Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section. 
* Include the following artifacts in the build script:
  <var name="artifact_name" value="ktor-serialization"/>
  <include src="lib.md" include-id="add_ktor_artifact"/>


## Register the Json Converter {id="register_json_converter"}
To register the JSON converter in your application, call the `json` method:
```kotlin
import io.ktor.serialization.*

install(ContentNegotiation) {
    json()
}
```
Inside the `json` method, you can access the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html) API, for example:
```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
        // ...
    })
}
```
To learn how to receive and send data, see [](serialization.md#receive_send_data).

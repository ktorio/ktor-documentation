[//]: # (title: kotlinx.serialization)

ContentNegotiation for [server](serialization-server.md) and [client](serialization-client.md) allows you to use content converters provided by the [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) library. This library supports JSON, CBOR, ProtoBuf, and other formats.


## Add dependencies {id="add_dependencies"}
Before registering a required converter, perform the following steps:

1. Add the Kotlin serialization plugin, as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.
1. Include the following artifacts in the build script: 
    <var name="artifact_name" value="ktor-shared-serialization-kotlinx"/>
    <include src="lib.md" include-id="add_ktor_artifact"/>
   
    This will be enough for converting JSON. 
1. (Optional) To convert other formats (for example, CBOR or ProtoBuf), you need to include a corresponding artifact. Learn more from the [Formats](https://kotlinlang.org/docs/serialization.html#formats) section.


## Register a converter {id="register_converter"}

### Register the JSON converter {id="register_json_converter"}
To register the JSON converter in your application, call the `json` method:
```kotlin
import io.ktor.shared.serializaion.kotlinx.*

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
To learn how to receive and send data, see [server](serialization-server.md#receive_send_data) or [client](serialization-client.md#receive_send_data) docs.


### Register an arbitrary converter {id="register_arbitrary_converter"}

To register an arbitrary converter from the kotlinx.serialization library (such as Protobuf or CBOR), call the [serialization](https://api.ktor.io/%ktor_version%/io.ktor.serialization/serialization.html) method and pass two parameters:
* The required [ContentType](https://api.ktor.io/%ktor_version%/io.ktor.http/-content-type/index.html) value.
* An object of the class implementing the required encoder/decoder. 
  
For example, you can register the [Cbor](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor/index.html) converter in the following way:
```kotlin
install(ContentNegotiation) {
    serialization(ContentType.Application.Cbor, Cbor.Default)
}
```

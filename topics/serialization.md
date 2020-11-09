[//]: # (title: Serialization and Content Negotiation)

The [ContentNegotiation](https://api.ktor.io/%ktor_version%/io.ktor.features/-content-negotiation/index.html) feature enables automatic conversion of request/response data and uses the following HTTP headers:

* The `Content-Type` header is used to detect the type of incoming data.
* The `Accept` header is used to select the converter type for sending data.

`ContentNegotiation` can be useful for deserializing JSON to a specified class object and vice versa. In Ktor, you can choose from the built-in JSON converters or implement your own conversion logic.


## Install ContentNegotiation {id="install_feature"}

<var name="feature_name" value="ContentNegotiation"/>
<include src="lib.md" include-id="install_feature"/>


## Register a Converter {id="register_converter"}

To register a converter for a specified `Content-Type`, you need to call the [register](https://api.ktor.io/%ktor_version%/io.ktor.features/-content-negotiation/-configuration/register.html) method. In the example below, two [custom converters](#implement_custom_converter) are registered to deserialize `application/json` and `application/xml` data:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, CustomJsonConverter())
    register(ContentType.Application.Xml, CustomXmlConverter())
}
```

### Built-in JSON Converters {id="json_converters"}
Ktor provides the set of built-in converters for handing JSON content without writing your own logic:

* [Gson](gson.md)
* [Jackson](jackson.md)
* [kotlinx.serialization](kotlin_serialization.md)

See a corresponding topic to learn how to install the required dependencies, register, and configure a converter.


## Receive and Send Data {id="receive_send_data"}

### Create a Data Class {id="create_data_class"}
To deserialize received data into an object, you need to create a data class, for example:
```kotlin
data class Customer(val id: Int, val firstName: String, val lastName: String)
```
If you use [kotlinx.serialization](kotlin_serialization.md), make sure that this class has the `@Serializable` annotation:
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)
```

### Receive Data {id="receive_data"}
To receive and convert a content for a request, call the [receive](https://api.ktor.io/%ktor_version%/io.ktor.request/receive.html) method that accepts a data class as a parameter:
```kotlin
post("/customer") {
    val customer = call.receive<Customer>()
}
```
The `Content-Type` of the request will be used to determine a [converter](#register_converter), which will be used to process the request. The example below shows a sample [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) request containing JSON data that will be converted to a `Customer` object on the server side:

```HTTP
POST http://0.0.0.0:8080/customer
Content-Type: application/json

{
  "id": 1,
  "firstName" : "Jet",
  "lastName": "Brains"
}
```

### Send Data {id="send_data"}
To pass a data object in a response, you can use the [respond](https://api.ktor.io/%ktor_version%/io.ktor.response/respond.html) method:
```kotlin
post("/customer") {
    call.respond(Customer(1, "Jet", "Brains"))
}
```
In this case, Ktor uses the `Accept` header to choose the required [converter](#register_converter).



## Implement a Custom Converter {id="implement_custom_converter"}

In Ktor, you can write your own [converter](#register_converter) for serializing/deserializing data. To do this, you need to implement the [ContentConverter](https://api.ktor.io/%ktor_version%/io.ktor.features/-content-converter/index.html) interface:
```kotlin
interface ContentConverter {
    suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any?
    suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
}
```
Take a look at the [GsonConverter](https://github.com/ktorio/ktor/blob/master/ktor-features/ktor-gson/jvm/src/io/ktor/gson/GsonSupport.kt) class as an implementation example.  



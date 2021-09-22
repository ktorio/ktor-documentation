[//]: # (title: Migrating from 1.6.x to 2.0.0)

This guide provides instructions on how to migrate your Ktor application from the 1.6.x version to 2.0.0.

## Ktor Server {id="server"}
### Server code is moved to the 'io.ktor.server.*' package {id="server-package"}
To unify and better distinguish the server and client APIs, server code is moved to the `io.ktor.server.*` package ([KTOR-2865](https://youtrack.jetbrains.com/issue/KTOR-2865)).
This means that you need to update [dependencies](#server-package-dependencies) for and [imports](#server-package-imports) in your application, as shown below.

#### Dependencies {id="server-package-dependencies"}
| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [Locations](locations.md) | `io.ktor:ktor-locations` | `io.ktor:ktor-server-locations` |
| [Webjars](webjars.md) | `io.ktor:ktor-webjars` | `io.ktor:ktor-server-webjars` |
| [HTML DSL](html_dsl.md) | `io.ktor:ktor-html-builder` | `io.ktor:ktor-server-html-builder` |
| [FreeMarker](freemarker.md) | `io.ktor:ktor-freemarker` | `io.ktor:ktor-server-freemarker` |
| [Velocity](velocity.md) | `io.ktor:ktor-velocity` | `io.ktor:ktor-server-velocity` |
| [Mustache](mustache.md) | `io.ktor:ktor-mustache` | `io.ktor:ktor-server-mustache` |
| [Thymeleaf](thymeleaf.md) | `io.ktor:ktor-thymeleaf` | `io.ktor:ktor-server-thymeleaf` |
| [Pebble](pebble.md) | `io.ktor:ktor-pebble` | `io.ktor:ktor-server-pebble` |
| [kotlinx.serialization](kotlin_serialization.md) | `io.ktor:ktor-serialization` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-kotlinx` |
| [Gson](gson.md) | `io.ktor:ktor-gson` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-gson` |
| [Jackson](jackson.md) | `io.ktor:ktor-jackson` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-jackson` |
| [Authentication](authentication.md) | `io.ktor:ktor-auth` | `io.ktor:ktor-server-auth` |
| [JWT authentication](jwt.md) | `io.ktor:ktor-auth-jwt` | `io.ktor:ktor-server-auth-jwt` |
| [LDAP authentication](ldap.md) | `io.ktor:ktor-auth-ldap` | `io.ktor:ktor-server-auth-ldap` |
| [WebSockets](websocket.md) | `io.ktor:ktor-websockets` | `io.ktor:ktor-server-websockets` |
| [Micrometer metric](micrometer_metrics.md) | `io.ktor:ktor-metrics-micrometer` | `io.ktor:ktor-server-metrics-micrometer` |
| [Dropwizard metrics](dropwizard_metrics.md) | `io.ktor:ktor-metrics` | `io.ktor:ktor-server-metrics` |


#### Imports {id="server-package-imports"}
| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [Application](create_server.xml) | `import io.ktor.application.*` | `import io.ktor.server.application.*` |
| [Configuration](Configurations.xml) | `import io.ktor.config.*` | `import io.ktor.server.config.*` |
| [Routing](Routing_in_Ktor.md) | `import io.ktor.routing.*` | `import io.ktor.server.routing.*` |
| [Requests](requests.md) | `import io.ktor.request.*` | `import io.ktor.server.request.*` |
| [Responses](responses.md) | `import io.ktor.response.*` | `import io.ktor.server.response.*` |
| [Plugins](#feature-plugin) | `import io.ktor.features.*` | `import io.ktor.server.plugins.*` |
| [Locations](locations.md) | `import io.ktor.locations.*` | `import io.ktor.server.locations.*` |
| [Static content](Serving_Static_Content.md) | `import io.ktor.http.content.*` | `import io.ktor.server.http.content.*` |
| [HTML DSL](html_dsl.md) | `import io.ktor.html.*` | `import io.ktor.server.html.*` |
| [FreeMarker](freemarker.md) | `import io.ktor.freemarker.*` | `import io.ktor.server.freemarker.*` |
| [Velocity](velocity.md) | `import io.ktor.velocity.*` | `import io.ktor.server.velocity.*` |
| [Mustache](mustache.md) | `import io.ktor.mustache.*` | `import io.ktor.server.mustache.*` |
| [Thymeleaf](thymeleaf.md) | `import io.ktor.thymeleaf.*` | `import io.ktor.server.thymeleaf.*` |
| [Pebble](pebble.md) | `import io.ktor.pebble.*` | `import io.ktor.server.pebble.*` |
| [kotlinx.serialization](kotlin_serialization.md) | `import io.ktor.serialization.*` | `import io.ktor.shared.serialization.kotlinx.*` |
| [Gson](gson.md) | `import io.ktor.gson.*` | `import io.ktor.shared.serializaion.gson.*` |
| [Jackson](jackson.md) | `import io.ktor.jackson.*` | `import io.ktor.shared.serializaion.jackson.*` |
| [Authentication](authentication.md) | `import io.ktor.auth.*` | `import io.ktor.server.auth.*` |
| [JWT authentication](jwt.md) | `import io.ktor.auth.jwt.*` | `import io.ktor.server.auth.jwt.*` |
| [LDAP authentication](ldap.md) | `import io.ktor.auth.ldap.*` | `import io.ktor.server.auth.ldap.*` |
| [Sessions](sessions.md) | `import io.ktor.sessions.*` | `import io.ktor.server.sessions.*` |
| [WebSockets](websocket.md) | `import io.ktor.websocket.*` | `import io.ktor.server.websocket.*` |
| [Micrometer metric](micrometer_metrics.md) | `import io.ktor.metrics.micrometer.*` | `import io.ktor.server.metrics.micrometer.*` |
| [Dropwizard metrics](dropwizard_metrics.md) | `import io.ktor.metrics.dropwizard.*` | `import io.ktor.server.metrics.dropwizard.*` |


### Feature is renamed to Plugin {id="feature-plugin"}

In Ktor 2.0.0, _Feature_ is renamed to _[Plugin](Plugins.md)_ to better describe functionality that intercepts the request/response pipeline ([KTOR-2326](https://youtrack.jetbrains.com/issue/KTOR-2326)).
This affects the entire Ktor API and requires updating your application as described below.

#### Imports {id="feature-plugin-imports"}
[Installing any plugin](Plugins.md#install) requires updating imports and also depends on [moving server code](#server-package-imports) to the `io.ktor.server.*` package:

| 1.6.x | 2.0.0 |
| :--- | ---: |
| `import io.ktor.features.*` | `import io.ktor.server.plugins.*` |

#### Custom plugins {id="feature-plugin-custom"}

Renaming Feature to Plugin introduces the following changes for API related to [custom plugins](Creating_custom_plugins.md):
* The `ApplicationFeature` interface is renamed to `ApplicationPlugin`.
* The `Features` [pipeline phase](Pipelines.md) is renamed to `Plugins`

> See [new plugins API]().


### Content negotiation and serialization {id="serialization"}

[Content negotiation and serialization](serialization.md) server API was refactored to reuse serialization libraries between the server and client.
The main changes are:
* `ContentNegotiation` is moved from `ktor-server-core` to a separate `ktor-server-content-negotiation` artifact.
* Serialization libraries are moved from `ktor-*` to the `ktor-shared-serialization-*` artifacts also used by the client.

You need to update [dependencies](#dependencies-serialization) for and [imports](#imports-serialization) in your application, as shown below.


#### Dependencies {id="dependencies-serialization"}

| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [ContentNegotiation](serialization.md) | `io.ktor:ktor-server-core` | `io.ktor:ktor-server-content-negotiation` |
| [kotlinx.serialization](kotlin_serialization.md) | `io.ktor:ktor-serialization` | `io.ktor:ktor-shared-serialization-kotlinx` |
| [Gson](gson.md) | `io.ktor:ktor-gson` | `io.ktor:ktor-shared-serialization-gson` |
| [Jackson](jackson.md) | `io.ktor:ktor-jackson` | `io.ktor:ktor-shared-serialization-jackson` |

#### Imports {id="imports-serialization"}
| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [kotlinx.serialization](kotlin_serialization.md) | `import io.ktor.serialization.*` | `import io.ktor.shared.serialization.kotlinx.*` |
| [Gson](gson.md) | `import io.ktor.gson.*` | `import io.ktor.shared.serializaion.gson.*` |
| [Jackson](jackson.md) | `import io.ktor.jackson.*` | `import io.ktor.shared.serializaion.jackson.*` |

#### Custom converters {id="serialization-custom-converter"}

Signatures of functions exposed by the [ContentConverter](serialization.md#implement_custom_converter) interface are changed in the following way:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
interface ContentConverter {
    suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any?
    suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
interface ContentConverter {
    suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any): OutgoingContent?
    suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any?
}
```

</tab>
</tabs>




## Ktor Client {id="client"}
### Requests and responses {id="request-response"}

In v2.0.0, API used to make requests and receive responses is updated to make it more consistent and discoverable ([KTOR-29](https://youtrack.jetbrains.com/issue/KTOR-29)).

#### Request functions {id="request-overloads"}

[Request functions](request.md) with multiple parameters are deprecated. For example, the `port` and `path` parameters need to be replaced with a single `url` parameter:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client.get(port = 8080, path = "/customer/3")
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.get("http://0.0.0.0:8080/customer/3")
```

</tab>
</tabs>

You can specify additional [request parameters](request.md#parameters) inside the request function lambda using API exposed by [HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html).


#### Request body {id="request-body"}

The `HttpRequestBuilder.body` property used to set the [request body](request.md#body) is replaced with the `HttpRequestBuilder.setBody` function:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client.post("http://localhost:8080/post") {
    body = "Body content"
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.post("http://localhost:8080/post") {
    setBody("Body content")
}
```

</tab>
</tabs>




#### Responses {id="responses"}
With v2.0.0, request functions (such as `get`, `post`, `put`, [submitForm](request.md#form_parameters), and so on) don't accept generic arguments for receiving an object of a specific type.
Now all request functions return a `HttpResponse` object, which exposes the `body` function with a generic argument for receiving a specific type instance.
You can also use `bodyAsText` or `bodyAsChannel` to receive content as a string or channel.

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
val httpResponse: HttpResponse = client.get("https://ktor.io/")
val stringBody: String = httpResponse.receive()
val byteArrayBody: ByteArray = httpResponse.receive()
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
val httpResponse: HttpResponse = client.get("https://ktor.io/")
val stringBody: String = httpResponse.body()
val byteArrayBody: ByteArray = httpResponse.body()
```

</tab>
</tabs>

With the [ContentNegotiation](json.md) plugin installed, you can receive an arbitrary object as follows:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
val customer: Customer = client.get("http://localhost:8080/customer/3")
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
val customer: Customer = client.get("http://localhost:8080/customer/3").body()
```

</tab>
</tabs>


#### Streaming responses {id="streaming-response"}
Due to [removing generic arguments](#responses) from request functions, receiving a streaming response requires separate functions.
To achieve this, functions with the `prepare` prefix are added, such as `prepareGet` or `preparePost`:

```kotlin
public suspend fun HttpClient.prepareGet(builder: HttpRequestBuilder): HttpStatement
public suspend fun HttpClient.preparePost(builder: HttpRequestBuilder): HttpStatement
```

The example below shows how to change your code in this case:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client.get<HttpStatement>("https://ktor.io/").execute { httpResponse ->
    val channel: ByteReadChannel = httpResponse.receive()
    while (!channel.isClosedForRead) {
        // Read data
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.prepareGet("https://ktor.io/").execute { httpResponse ->
    val channel: ByteReadChannel = httpResponse.body()
    while (!channel.isClosedForRead) {
        // Read data
    }
}
```

</tab>
</tabs>

You can find the full example here: [](response.md#streaming).



### Content negotiation and serialization {id="serialization-client"}

The Ktor client now supports content negotiation and shares serialization libraries with the Ktor server.
The main changes are:
* `JsonFeature` is deprecated in favor of `ContentNegotiation`, which can be found in the `ktor-client-content-negotiation` artifact.
* Serialization libraries are moved from `ktor-client-*` to the `ktor-shared-serialization-*` artifacts.

You need to update [dependencies](#imports-dependencies-client) for and [imports](#imports-serialization-client) in your client code, as shown below.

#### Dependencies {id="imports-dependencies-client"}

| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| `ContentNegotiation` | n/a | `io.ktor:ktor-client-content-negotiation` |
| kotlinx.serialization | `io.ktor:ktor-client-serialization` | `io.ktor:ktor-shared-serialization-kotlinx` |
| Gson | `io.ktor:ktor-client-gson` | `io.ktor:ktor-shared-serialization-gson` |
| Jackson | `io.ktor:ktor-client-jackson` | `io.ktor:ktor-shared-serialization-jackson` |

#### Imports {id="imports-serialization-client"}
| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| `ContentNegotiation` | n/a | `import io.ktor.client.plugins.*` |
| kotlinx.serialization | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serialization.kotlinx.*` |
| Gson | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serializaion.gson.*` |
| Jackson | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serializaion.jackson.*` |

### Bearer authentication

The [refreshTokens](auth.md#bearer) function now uses the `RefreshTokenParams` instance as [lambda receiver](https://kotlinlang.org/docs/scope-functions.html#context-object-this-or-it) (`this`) instead of the `HttpResponse` lambda argument (`it`):

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
bearer {
    refreshTokens {  // it: HttpResponse
        // ...
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
bearer {
    refreshTokens { // this: RefreshTokenParams
        // ...
    }
}
```

</tab>
</tabs>

`RefreshTokenParams` exposes the following properties:
* `response` to access response parameters;
* `client` to make a request to refresh tokens;
* `oldTokens` to access tokens obtained using `loadTokens`.


### Feature is renamed to Plugin {id="feature-plugin-client"}

As for the Ktor server, _Feature_ is renamed to _Plugin_ in the client API.
This might affect your application, as described below.

#### Imports {id="feature-plugin-imports-client"}
Update imports for [installing plugins](http-client_plugins.md), for example:

| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [Default request](default-request.md), [User agent](user-agent.md), [Charsets](http-plain-text.md), [Response validation](response-validation.md), [Timeout](timeout.md), HttpCache, HttpSend | `import io.ktor.client.features.*` | `import io.ktor.client.plugins.*` |
| [Authentication](auth.md) | `import io.ktor.client.features.auth.*`, `import io.ktor.client.features.auth.providers.*` | `import io.ktor.client.plugins.auth.*`, `import io.ktor.client.plugins.auth.providers.*` |
| [Cookies](http-cookies.md) | `import io.ktor.client.features.cookies.*` | `import io.ktor.client.plugins.cookies.*` |
| [Logging](client_logging.md) | `import io.ktor.client.features.logging.*` | `import io.ktor.client.plugins.logging.*` |
| [WebSockets](websocket_client.md) | `import io.ktor.client.features.websocket.*` | `import io.ktor.client.plugins.websocket.*` |
| Content encoding | `import io.ktor.client.features.compression.*` | `import io.ktor.client.plugins.compression.*` |


#### Custom plugins {id="feature-plugin-custom-client"}
The `HttpClientFeature` interface is renamed to `HttpClientPlugin`.

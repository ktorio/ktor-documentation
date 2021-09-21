[//]: # (title: Migrating from 1.6.x to 2.0.0)

This guide provides the instructions how to migrate your Ktor application from the 1.6.x version to 2.0.0.

## Ktor Server {id="server"}
### Move server code to io.ktor.server.* package {id="server-package"}

#### Dependencies {id="dependencies"}
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


#### Imports {id="imports"}
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






### Renaming feature to plugin {id="feature-plugin"}

* Update imports for [installing plugins](Plugins.md#install):   
   `import io.ktor.features.*` -> `import io.ktor.server.plugins.*`
* Get a plugin instance:   
   `Application.feature` -> `Application.plugin`
* Exceptions:
   `MissingApplicationFeatureException` -> `MissingApplicationPluginException`
   
   `DuplicateApplicationFeatureException` -> `DuplicateApplicationPluginException`
* [Custom plugin](Creating_custom_plugins.md) interface:
  `ApplicationFeature` -> `ApplicationPlugin`
   > See new plugins API [link]().
* Pipeline phase:   
  `Features` -> `Plugins`


### Content negotiation and serialization {id="serialization"}
#### Dependencies {id="dependencies-serialization"}

1. `ContentNegotiation` moved from `ktor-server-core` to `ktor-server-content-negotiation`.
2. Serialization moved from `ktor-*` to `ktor-shared-serialization` (link to client serialization).

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

#### Custom converter {id="custom-converter"}
1.6.x:
```kotlin
interface ContentConverter {
    suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any?
    suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
}
```

2.0.0: 

```kotlin
interface ContentConverter {
    suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any): OutgoingContent?  
    suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any?
}
```




## Ktor Client {id="client"}

### Making requests {id="make-request"}

#### Request methods overloads {id="request-overloads"}

Deprecate builder methods with multiple parameters. Example:

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


#### Request body {id="request-body"}
[Specify request body](request.md#body):

The `HttpRequestBuilder.body` property is replaced with the `HttpRequestBuilder.setBody` function. Example:

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




### Receiving responses {id="receive-response"}
Remove generic argument from the methods that we keep.
Request functions now return a [response](response.md) only as a `HttpResponse` object. You can use `body`, `bodyAsText`, `bodyAsChannel`. Examples:

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

With the ContentNegotiation/serialization installed:

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

The same is for other request methods: `post`, `put`, [submitForm](request.md#form_parameters)

#### Streaming responses {id="streaming-response"}
Due to removing generic arguments, we need to provide methods that return `HttpStatement` for streaming responses:

```kotlin
public suspend fun HttpClient.prepareGet(builder: HttpRequestBuilder): HttpStatement
public suspend fun HttpClient.preparePost(builder: HttpRequestBuilder): HttpStatement
```

Example:

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

[Full example](response.md#streaming)



### Content negotiation and serialization {id="serialization-client"}

[](json.md) changes:

Link to server:

1. `JsonFeature` is deprecated, use `ContentNegotiation` for client `io.ktor:ktor-client-content-negotiation`.
2. Serialization moved from `ktor-client-*` to `ktor-shared-serialization` (link to server serialization).

| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| `ContentNegotiation` | n/a | `io.ktor:ktor-client-content-negotiation` |
| kotlinx.serialization | `io.ktor:ktor-client-serialization` | `io.ktor:ktor-shared-serialization-kotlinx` |
| Gson | `io.ktor:ktor-client-gson` | `io.ktor:ktor-shared-serialization-gson` |
| Jackson | `io.ktor:ktor-client-jackson` | `io.ktor:ktor-shared-serialization-jackson` |

#### Imports {id="imports-serialization-client"}
| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| kotlinx.serialization | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serialization.kotlinx.*` |
| Gson | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serializaion.gson.*` |
| Jackson | `import io.ktor.client.features.json.*` | `import io.ktor.shared.serializaion.jackson.*` |

### Bearer authentication

[Bearer authentication](auth.md#bearer):

`refreshTokens` lambda now accepts the `RefreshTokenParams` instead of a `HttpResponse`. 

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

You can use `RefreshTokenParams` to access a response (`RefreshTokenParams.response`), the HTTP client (`RefreshTokenParams.client`) to make a request to refresh tokens, and old tokens (`RefreshTokenParams.oldTokens`).



### Renaming feature to plugin {id="feature-plugin-client"}

* Update imports for [installing plugins](http-client_plugins.md), for example:
   
   ```kotlin
   import io.ktor.client.features.* -> import io.ktor.client.plugins.*
   import io.ktor.client.features.auth.* -> import io.ktor.client.plugins.auth.*
   ```
* Get a plugin instance: HttpClient.feature -> HttpClient.plugin
* Custom feature: `HttpClientFeature` -> `HttpClientPlugin`

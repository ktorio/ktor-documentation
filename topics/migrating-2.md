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


### Content negotiation and serialization
#### Dependencies {id="dependencies-serialization"}

1. `ContentNegotiation` moved from `ktor-server-core` to `ktor-server-content-negotiation`.
2. Serialization moved from `ktor-*` to `ktor-shared-serialization` (link to client serialization).

| Subsystem | 1.6.x | 2.0.0 |
| :---        |    :----:   |          ---: |
| [kotlinx.serialization](kotlin_serialization.md) | `io.ktor:ktor-serialization` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-kotlinx` |
| [Gson](gson.md) | `io.ktor:ktor-gson` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-gson` |
| [Jackson](jackson.md) | `io.ktor:ktor-jackson` | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-shared-serialization-jackson` |

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
Deprecate builder methods with multiple parameters  
`client.get(port = 8080, path = "/v1/item/$key")`

`client.get("http://0.0.0.0:8080/v1/item/$key")`




### Receiving responses {id="receive-response"}

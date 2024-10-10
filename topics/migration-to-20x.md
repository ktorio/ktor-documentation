[//]: # (title: Migrating from 1.6.x to 2.0.x)

<show-structure for="chapter" depth="2"/>

This guide provides instructions on how to migrate your Ktor application from the 1.6.x version to 2.0.x.

## Ktor Server {id="server"}
### Server code is moved to the 'io.ktor.server.*' package {id="server-package"}
To unify and better distinguish the server and client APIs, server code is moved to the `io.ktor.server.*` package ([KTOR-2865](https://youtrack.jetbrains.com/issue/KTOR-2865)).
This means that you need to update [dependencies](#server-package-dependencies) for and [imports](#server-package-imports) in your application, as shown below.

#### Dependencies {id="server-package-dependencies"}
| Subsystem                                           |               1.6.x               |                                                                                2.0.0 |
|:----------------------------------------------------|:---------------------------------:|-------------------------------------------------------------------------------------:|
| [Locations](https://ktor.io/docs/server-locations.html)                    |     `io.ktor:ktor-locations`      |                                                      `io.ktor:ktor-server-locations` |
| [Webjars](server-webjars.md)                        |      `io.ktor:ktor-webjars`       |                                                        `io.ktor:ktor-server-webjars` |
| [AutoHeadResponse](server-autoheadresponse.md)      |    `io.ktor:ktor-server-core`     |                                             `io.ktor:ktor-server-auto-head-response` |
| [StatusPages](server-status-pages.md)               |    `io.ktor:ktor-server-core`     |                                                   `io.ktor:ktor-server-status-pages` |
| [CallId](server-call-id.md)                         |    `io.ktor:ktor-server-core`     |                                                        `io.ktor:ktor-server-call-id` |
| [DoubleReceive](server-double-receive.md)           |    `io.ktor:ktor-server-core`     |                                                 `io.ktor:ktor-server-double-receive` |
| [HTML DSL](server-html-dsl.md)                      |    `io.ktor:ktor-html-builder`    |                                                   `io.ktor:ktor-server-html-builder` |
| [FreeMarker](server-freemarker.md)                  |     `io.ktor:ktor-freemarker`     |                                                     `io.ktor:ktor-server-freemarker` |
| [Velocity](server-velocity.md)                      |      `io.ktor:ktor-velocity`      |                                                       `io.ktor:ktor-server-velocity` |
| [Mustache](server-mustache.md)                      |      `io.ktor:ktor-mustache`      |                                                       `io.ktor:ktor-server-mustache` |
| [Thymeleaf](server-thymeleaf.md)                    |     `io.ktor:ktor-thymeleaf`      |                                                      `io.ktor:ktor-server-thymeleaf` |
| [Pebble](server-pebble.md)                          |       `io.ktor:ktor-pebble`       |                                                         `io.ktor:ktor-server-pebble` |
| [kotlinx.serialization](server-serialization.md)    |   `io.ktor:ktor-serialization`    | `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-serialization-kotlinx-json` |
| [Gson](server-serialization.md)                     |        `io.ktor:ktor-gson`        |         `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-serialization-gson` |
| [Jackson](server-serialization.md)                  |      `io.ktor:ktor-jackson`       |      `io.ktor:ktor-server-content-negotiation`, `io.ktor:ktor-serialization-jackson` |
| [Authentication](server-auth.md)                    |        `io.ktor:ktor-auth`        |                                                           `io.ktor:ktor-server-auth` |
| [JWT authentication](server-jwt.md)                 |      `io.ktor:ktor-auth-jwt`      |                                                       `io.ktor:ktor-server-auth-jwt` |
| [LDAP authentication](server-ldap.md)               |     `io.ktor:ktor-auth-ldap`      |                                                      `io.ktor:ktor-server-auth-ldap` |
| [DataConversion](server-data-conversion.md)         |    `io.ktor:ktor-server-core`     |                                                `io.ktor:ktor-server-data-conversion` |
| [DefaultHeaders](server-default-headers.md)         |    `io.ktor:ktor-server-core`     |                                                `io.ktor:ktor-server-default-headers` |
| [Compression](server-compression.md)                |    `io.ktor:ktor-server-core`     |                                                    `io.ktor:ktor-server-compression` |
| [CachingHeaders](server-caching-headers.md)         |    `io.ktor:ktor-server-core`     |                                                `io.ktor:ktor-server-caching-headers` |
| [ConditionalHeaders](server-conditional-headers.md) |    `io.ktor:ktor-server-core`     |                                            `io.ktor:ktor-server-conditional-headers` |
| [CORS](server-cors.md)                              |    `io.ktor:ktor-server-core`     |                                                           `io.ktor:ktor-server-cors` |
| [Forwarded headers](server-forward-headers.md)      |    `io.ktor:ktor-server-core`     |                                               `io.ktor:ktor-server-forwarded-header` |
| [HSTS](server-hsts.md)                              |    `io.ktor:ktor-server-core`     |                                                           `io.ktor:ktor-server-hsts` |
| [HttpsRedirect](server-https-redirect.md)           |    `io.ktor:ktor-server-core`     |                                                  `io.ktor:ktor-server-http-redirect` |
| [PartialContent](server-partial-content.md)         |    `io.ktor:ktor-server-core`     |                                                `io.ktor:ktor-server-partial-content` |
| [WebSockets](server-websockets.md)                  |     `io.ktor:ktor-websockets`     |                                                     `io.ktor:ktor-server-websockets` |
| [CallLogging](server-call-logging.md)               |    `io.ktor:ktor-server-core`     |                                                   `io.ktor:ktor-server-call-logging` |
| [Micrometer metric](server-metrics-micrometer.md)   | `io.ktor:ktor-metrics-micrometer` |                                             `io.ktor:ktor-server-metrics-micrometer` |
| [Dropwizard metrics](server-metrics-dropwizard.md)    |      `io.ktor:ktor-metrics`       |                                                        `io.ktor:ktor-server-metrics` |
| [Sessions](server-sessions.md)                      |      `io.ktor:ktor-server-core`   |                                        `io.ktor:ktor-server-sessions` |

> To add all plugins at once, you can use the `io.ktor:ktor-server` artifact.


#### Imports {id="server-package-imports"}
| Subsystem                                           |                 1.6.x                 |                                                2.0.0 |
|:----------------------------------------------------|:-------------------------------------:|-----------------------------------------------------:|
| [Application](server-create-and-configure.topic)    |    `import io.ktor.application.*`     |                `import io.ktor.server.application.*` |
| [Configuration](server-configuration-file.topic)    |       `import io.ktor.config.*`       |                     `import io.ktor.server.config.*` |
| [Routing](server-routing.md)                        |      `import io.ktor.routing.*`       |                    `import io.ktor.server.routing.*` |
| [AutoHeadResponse](server-autoheadresponse.md)      |      `import io.ktor.features.*`      |           `import io.ktor.server.plugins.autohead.*` |
| [StatusPages](server-status-pages.md)               |      `import io.ktor.features.*`      |        `import io.ktor.server.plugins.statuspages.*` |
| [CallId](server-call-id.md)                         |      `import io.ktor.features.*`      |             `import io.ktor.server.plugins.callid.*` |
| [DoubleReceive](server-double-receive.md)           |      `import io.ktor.features.*`      |      `import io.ktor.server.plugins.doublereceive.*` |
| [Requests](server-requests.md)                      |      `import io.ktor.request.*`       |                    `import io.ktor.server.request.*` |
| [Responses](server-responses.md)                    |      `import io.ktor.response.*`      |                   `import io.ktor.server.response.*` |
| [Plugins](#feature-plugin)                          |      `import io.ktor.features.*`      |                    `import io.ktor.server.plugins.*` |
| [Locations](https://ktor.io/docs/server-locations.html)                    |     `import io.ktor.locations.*`      |                  `import io.ktor.server.locations.*` |
| [Static content](server-static-content.md)          |    `import io.ktor.http.content.*`    |               `import io.ktor.server.http.content.*` |
| [HTML DSL](server-html-dsl.md)                      |        `import io.ktor.html.*`        |                       `import io.ktor.server.html.*` |
| [FreeMarker](server-freemarker.md)                  |     `import io.ktor.freemarker.*`     |                 `import io.ktor.server.freemarker.*` |
| [Velocity](server-velocity.md)                      |      `import io.ktor.velocity.*`      |                   `import io.ktor.server.velocity.*` |
| [Mustache](server-mustache.md)                      |      `import io.ktor.mustache.*`      |                   `import io.ktor.server.mustache.*` |
| [Thymeleaf](server-thymeleaf.md)                    |     `import io.ktor.thymeleaf.*`      |                  `import io.ktor.server.thymeleaf.*` |
| [Pebble](server-pebble.md)                          |       `import io.ktor.pebble.*`       |                     `import io.ktor.server.pebble.*` |
| [ContentNegotiation](server-serialization.md)       |      `import io.ktor.features.*`      | `import io.ktor.server.plugins.contentnegotiation.*` |
| [kotlinx.serialization](server-serialization.md)    |   `import io.ktor.serialization.*`    |        `import io.ktor.serialization.kotlinx.json.*` |
| [Gson](server-serialization.md)                     |        `import io.ktor.gson.*`        |                `import io.ktor.serialization.gson.*` |
| [Jackson](server-serialization.md)                  |      `import io.ktor.jackson.*`       |             `import io.ktor.serialization.jackson.*` |
| [Authentication](server-auth.md)                    |        `import io.ktor.auth.*`        |                       `import io.ktor.server.auth.*` |
| [JWT authentication](server-jwt.md)                 |      `import io.ktor.auth.jwt.*`      |                   `import io.ktor.server.auth.jwt.*` |
| [LDAP authentication](server-ldap.md)               |     `import io.ktor.auth.ldap.*`      |                  `import io.ktor.server.auth.ldap.*` |
| [Sessions](server-sessions.md)                      |      `import io.ktor.sessions.*`      |                   `import io.ktor.server.sessions.*` |
| [DefaultHeaders](server-default-headers.md)         |      `import io.ktor.features.*`      |     `import io.ktor.server.plugins.defaultheaders.*` |
| [Compression](server-compression.md)                |      `import io.ktor.features.*`      |        `import io.ktor.server.plugins.compression.*` |
| [CachingHeaders](server-caching-headers.md)         |      `import io.ktor.features.*`      |     `import io.ktor.server.plugins.cachingheaders.*` |
| [ConditionalHeaders](server-conditional-headers.md) |      `import io.ktor.features.*`      | `import io.ktor.server.plugins.conditionalheaders.*` |
| [CORS](server-cors.md)                              |      `import io.ktor.features.*`      |               `import io.ktor.server.plugins.cors.*` |
| [Forwarded headers](server-forward-headers.md)      |      `import io.ktor.features.*`      |   `import io.ktor.server.plugins.forwardedheaders.*` |
| [HSTS](server-hsts.md)                              |      `import io.ktor.features.*`      |               `import io.ktor.server.plugins.hsts.*` |
| [HttpsRedirect](server-https-redirect.md)           |      `import io.ktor.features.*`      |      `import io.ktor.server.plugins.httpsredirect.*` |
| [PartialContent](server-partial-content.md)         |      `import io.ktor.features.*`      |     `import io.ktor.server.plugins.partialcontent.*` |
| [WebSockets](server-websockets.md)                  |     `import io.ktor.websocket.*`      |                  `import io.ktor.server.websocket.*` |
| [CallLogging](server-call-logging.md)               |      `import io.ktor.features.*`      |         `import io.ktor.server.plugins.callloging.*` |
| [Micrometer metric](server-metrics-micrometer.md)   | `import io.ktor.metrics.micrometer.*` |         `import io.ktor.server.metrics.micrometer.*` |
| [Dropwizard metrics](server-metrics-dropwizard.md)    | `import io.ktor.metrics.dropwizard.*` |         `import io.ktor.server.metrics.dropwizard.*` |


### WebSockets code is moved to the 'websockets' package {id="server-ws-package"}

WebSockets code is moved from `http-cio` to the `websockets` package. This requires updating imports as follows:

| 1.6.x                                 |                        2.0.0 |
|:--------------------------------------|-----------------------------:|
| `import io.ktor.http.cio.websocket.*` | `import io.ktor.websocket.*` |

Note that this change also affects the [client](#client-ws-package).


### Feature is renamed to Plugin {id="feature-plugin"}

In Ktor 2.0.0, _Feature_ is renamed to _[Plugin](server-plugins.md)_ to better describe functionality that intercepts the request/response pipeline ([KTOR-2326](https://youtrack.jetbrains.com/issue/KTOR-2326)).
This affects the entire Ktor API and requires updating your application as described below.

#### Imports {id="feature-plugin-imports"}
[Installing any plugin](server-plugins.md#install) requires updating imports and also depends on [moving server code](#server-package-imports) to the `io.ktor.server.*` package:

| 1.6.x                       |                             2.0.0 |
|:----------------------------|----------------------------------:|
| `import io.ktor.features.*` | `import io.ktor.server.plugins.*` |

#### Custom plugins {id="feature-plugin-custom"}

Renaming Feature to Plugin introduces the following changes for API related to [custom plugins](server-custom-plugins-base-api.md):
* The `ApplicationFeature` interface is renamed to `BaseApplicationPlugin`.
* The `Features` [pipeline phase](server-custom-plugins-base-api.md#pipelines) is renamed to `Plugins`.

> Note that starting with v2.0.0, Ktor provides the new API for [creating custom plugins](server-custom-plugins.md). In general, this API doesn't require an understanding of internal Ktor concepts, such as pipelines, phases, and so on. Instead, you have access to different stages of handling requests and responses using various handlers, such as `onCall`, `onCallReceive`, `onCallRespond`, and so on. You can learn how pipeline phases map to handlers in a new API from this section: [](server-custom-plugins-base-api.md#mapping).


### Content negotiation and serialization {id="serialization"}

[Content negotiation and serialization](server-serialization.md) server API was refactored to reuse serialization libraries between the server and client.
The main changes are:
* `ContentNegotiation` is moved from `ktor-server-core` to a separate `ktor-server-content-negotiation` artifact.
* Serialization libraries are moved from `ktor-*` to the `ktor-serialization-*` artifacts also used by the client.

You need to update [dependencies](#dependencies-serialization) for and [imports](#imports-serialization) in your application, as shown below.


#### Dependencies {id="dependencies-serialization"}

| Subsystem                                 |            1.6.x             |                                     2.0.0 |
|:------------------------------------------|:----------------------------:|------------------------------------------:|
| [ContentNegotiation](server-serialization.md)    |  `io.ktor:ktor-server-core`  | `io.ktor:ktor-server-content-negotiation` |
| [kotlinx.serialization](server-serialization.md) | `io.ktor:ktor-serialization` | `io.ktor:ktor-serialization-kotlinx-json` |
| [Gson](server-serialization.md)                  |     `io.ktor:ktor-gson`      |         `io.ktor:ktor-serialization-gson` |
| [Jackson](server-serialization.md)               |    `io.ktor:ktor-jackson`    |      `io.ktor:ktor-serialization-jackson` |

#### Imports {id="imports-serialization"}
| Subsystem                                 |              1.6.x               |                                         2.0.0 |
|:------------------------------------------|:--------------------------------:|----------------------------------------------:|
| [kotlinx.serialization](server-serialization.md) | `import io.ktor.serialization.*` | `import io.ktor.serialization.kotlinx.json.*` |
| [Gson](server-serialization.md)                  |     `import io.ktor.gson.*`      |         `import io.ktor.serialization.gson.*` |
| [Jackson](server-serialization.md)               |    `import io.ktor.jackson.*`    |      `import io.ktor.serialization.jackson.*` |

#### Custom converters {id="serialization-custom-converter"}

Signatures of functions exposed by the [ContentConverter](server-serialization.md#implement_custom_serializer) interface are changed in the following way:

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


### Testing API {id="testing-api"}

With v2.0.0, the Ktor server uses a new API for [testing](server-testing.md), which solves various issues described in [KTOR-971](https://youtrack.jetbrains.com/issue/KTOR-971). The main changes are:
* The `withTestApplication`/`withApplication` functions are replaced with a new `testApplication` function.
* Inside the `testApplication` function, you need to use the existing [Ktor client](client-create-and-configure.md) instance to make requests to your server and verify the results.
* To test specific functionalities (for example, cookies or WebSockets), you need to create a new client instance and install a corresponding [plugin](client-plugins.md).

Let's take a look at several examples of migrating 1.6.x tests to 2.0.0:

#### Basic server test {id="basic-test"}

In the test below, the `handleRequest` function is replaced with the `client.get` request:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/engine-main/src/test/kotlin/EngineMainTest.kt"
include-lines="18-26"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/engine-main/src/test/kotlin/EngineMainTest.kt"
include-lines="11-16"}

</tab>
</tabs>


#### x-www-form-urlencoded {id="x-www-form-urlencoded"}

In the test below, the `handleRequest` function is replaced with the `client.post` request:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/post-form-parameters/src/test/kotlin/formparameters/ApplicationTest.kt"
include-lines="20-28"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/post-form-parameters/src/test/kotlin/formparameters/ApplicationTest.kt"
include-lines="11-18"}

</tab>
</tabs>


#### multipart/form-data {id="multipart-form-data"}

To build `multipart/form-data` in v2.0.0, you need to pass `MultiPartFormDataContent` to the client's `setBody` function:


<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/upload-file/src/test/kotlin/uploadfile/UploadFileTest.kt" include-lines="38-63"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/upload-file/src/test/kotlin/uploadfile/UploadFileTest.kt" include-lines="17-36"}

</tab>
</tabs>


#### JSON data {id="json-data"}

In v.1.6.x, you can serialize JSON data using the `Json.encodeToString` function provided by the `kotlinx.serialization` library.
With v2.0.0, you need to create a new client instance and install the [ContentNegotiation](client-serialization.md) plugin that allows serializing/deserializing the content in a specific format:


<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt"
include-lines="46-55"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt"
include-lines="31-44"}

</tab>
</tabs>


#### Preserve cookies during testing {id="preserving-cookies"}

In v1.6.x, `cookiesSession` is used to preserve cookies between requests when testing. With v2.0.0, you need to create a new client instance and install the [HttpCookies](client-cookies.md) plugin:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/session-cookie-client/src/test/kotlin/cookieclient/ApplicationTest.kt" include-lines="29-46"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/session-cookie-client/src/test/kotlin/cookieclient/ApplicationTest.kt" include-lines="12-27"}

</tab>
</tabs>

#### WebSockets {id="testing-ws"}

In the old API, `handleWebSocketConversation` is used to test [WebSocket conversations](server-websockets.md). With v2.0.0, you can test WebSocket conversations by using the [WebSockets](client-websockets.md) plugin provided by the client:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt"
include-lines="28-40"}

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
```
{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt"
include-lines="10-26"}

</tab>
</tabs>


### DoubleReceive {id="double-receive"}
With v2.0.0, the [DoubleReceive](server-double-receive.md) plugin configuration introduces the `cacheRawRequest` property, which is opposite to `receiveEntireContent`:
- In v1.6.x, the `receiveEntireContent` property is set to `false` by default.
- In v2.0.0, `cacheRawRequest` is set to `true` by default. The `receiveEntireContent` property is removed.


### Forwarded headers {id="forwarded-headers"}

In v2.0.0, the `ForwardedHeaderSupport` and `XForwardedHeaderSupport` plugins are renamed to [ForwardedHeaders](server-forward-headers.md) and `XForwardedHeaders`, respectively.


### Caching headers {id="caching-headers"}

The [options](server-caching-headers.md#configure) function used to define caching options now accepts the `ApplicationCall` as a lambda argument in addition to `OutgoingContent`: 

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
install(CachingHeaders) {
    options { outgoingContent ->
        // ...
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
install(CachingHeaders) {
    options { call, outgoingContent ->
        // ...
    }
}
```

</tab>
</tabs>


### Conditional headers {id="conditional-headers"}

The [version](server-conditional-headers.md#configure) function used to define a list of resource versions now accepts the `ApplicationCall` as a lambda argument in addition to `OutgoingContent`:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
install(ConditionalHeaders) {
    version { outgoingContent ->
        // ... 
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
install(ConditionalHeaders) {
    version { call, outgoingContent ->
        // ... 
    }
}
```

</tab>
</tabs>


### CORS {id="cors"}

Several functions used in [CORS](server-cors.md) configuration are renamed:
- `host` -> `allowHost`
- `header` -> `allowHeader`
- `method` -> `allowMethod`

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
install(CORS) {
    host("0.0.0.0:5000")
    header(HttpHeaders.ContentType)
    method(HttpMethod.Options)
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
install(CORS) {
    allowHost("0.0.0.0:5000")
    allowHeader(HttpHeaders.ContentType)
    allowMethod(HttpMethod.Options)
}
```

</tab>
</tabs>


### MicrometerMetrics {id="micrometer-metrics"}
In v1.6.x, the `baseName` property is used to specify the base name (prefix) of [Ktor metrics](server-metrics-micrometer.md) used for monitoring HTTP requests.
By default, it equals to `ktor.http.server`. 
With v2.0.0, `baseName` is replaced with `metricName` whose default value is `ktor.http.server.requests`.



## Ktor Client {id="client"}
### Requests and responses {id="request-response"}

In v2.0.0, API used to make requests and receive responses is updated to make it more consistent and discoverable ([KTOR-29](https://youtrack.jetbrains.com/issue/KTOR-29)).

#### Request functions {id="request-overloads"}

[Request functions](client-requests.md) with multiple parameters are deprecated. For example, the `port` and `path` parameters need to be replaced with a the `url` parameter exposed by [HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html):

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client.get(port = 8080, path = "/customer/3")
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.get { url(port = 8080, path = "/customer/3") }
```

</tab>
</tabs>

The `HttpRequestBuilder` also allows you to specify additional [request parameters](client-requests.md#parameters) inside the request function lambda.


#### Request body {id="request-body"}

The `HttpRequestBuilder.body` property used to set the [request body](client-requests.md#body) is replaced with the `HttpRequestBuilder.setBody` function:

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
With v2.0.0, request functions (such as `get`, `post`, `put`, [submitForm](client-requests.md#form_parameters), and so on) don't accept generic arguments for receiving an object of a specific type.
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

With the [ContentNegotiation](client-serialization.md) plugin installed, you can receive an arbitrary object as follows:

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

You can find the full example here: [](client-responses.md#streaming).



### Response validation {id="response-validation"}

With v2.0.0, the `expectSuccess` property used for [response validation](client-response-validation.md) is set to `false` by default.
This requires the following changes in your code:
- To [enable default validation](client-response-validation.md#default) and throw exceptions for non-2xx responses, set the `expectSuccess` property to `true`.
- If you [handle non-2xx exceptions](client-response-validation.md#non-2xx) using `handleResponseExceptionWithRequest`, you also need to enable `expectSuccess` explicitly.

#### HttpResponseValidator {id="http-response-validator"}

The [handleResponseException](client-response-validation.md#non-2xx) function is replaced with `handleResponseExceptionWithRequest`, which adds access to `HttpRequest` to provide additional information in exceptions:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
HttpResponseValidator {
    handleResponseException { exception ->
        // ...
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
HttpResponseValidator {
    handleResponseExceptionWithRequest { exception, request ->
        // ...
    }
}
```

</tab>
</tabs>



### Content negotiation and serialization {id="serialization-client"}

The Ktor client now supports content negotiation and shares serialization libraries with the Ktor server.
The main changes are:
* `JsonFeature` is deprecated in favor of `ContentNegotiation`, which can be found in the `ktor-client-content-negotiation` artifact.
* Serialization libraries are moved from `ktor-client-*` to the `ktor-serialization-*` artifacts.

You need to update [dependencies](#imports-dependencies-client) for and [imports](#imports-serialization-client) in your client code, as shown below.

#### Dependencies {id="imports-dependencies-client"}

| Subsystem             |                1.6.x                |                                     2.0.0 |
|:----------------------|:-----------------------------------:|------------------------------------------:|
| `ContentNegotiation`  |                 n/a                 | `io.ktor:ktor-client-content-negotiation` |
| kotlinx.serialization | `io.ktor:ktor-client-serialization` | `io.ktor:ktor-serialization-kotlinx-json` |
| Gson                  |     `io.ktor:ktor-client-gson`      |         `io.ktor:ktor-serialization-gson` |
| Jackson               |    `io.ktor:ktor-client-jackson`    |      `io.ktor:ktor-serialization-jackson` |

#### Imports {id="imports-serialization-client"}
| Subsystem             |                  1.6.x                  |                                                2.0.0 |
|:----------------------|:---------------------------------------:|-----------------------------------------------------:|
| `ContentNegotiation`  |                   n/a                   | `import io.ktor.client.plugins.contentnegotiation.*` |
| kotlinx.serialization | `import io.ktor.client.features.json.*` |        `import io.ktor.serialization.kotlinx.json.*` |
| Gson                  | `import io.ktor.client.features.json.*` |                `import io.ktor.serialization.gson.*` |
| Jackson               | `import io.ktor.client.features.json.*` |             `import io.ktor.serialization.jackson.*` |

### Bearer authentication

The [refreshTokens](client-bearer-auth.md) function now uses the `RefreshTokenParams` instance as [lambda receiver](https://kotlinlang.org/docs/scope-functions.html#context-object-this-or-it) (`this`) instead of the `HttpResponse` lambda argument (`it`):

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



### HttpSend {id="http-send"}

The API of the [HttpSend](client-http-send.md) plugin is changed as follows:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client[HttpSend].intercept { originalCall, request ->
    if (originalCall.something()) {
        val newCall = execute(request)
        // ...
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.plugin(HttpSend).intercept { request ->
    val originalCall = execute(request)
    if (originalCall.something()) {
        val newCall = execute(request)
        // ...
    }
}
```

</tab>
</tabs>

Note that with v2.0.0 indexed access is not available for accessing plugins. Use the [HttpClient.plugin](#client-get) function instead.

### The HttpClient.get(plugin: HttpClientPlugin) function is removed {id="client-get"}

With the 2.0.0 version, the `HttpClient.get` function accepting a client plugin is removed. Use the `HttpClient.plugin` function instead.

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
client.get(HttpSend).intercept { ... }
// or
client[HttpSend].intercept { ... }
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
client.plugin(HttpSend).intercept { ... }
```

</tab>
</tabs>


### Feature is renamed to Plugin {id="feature-plugin-client"}

As for the Ktor server, _Feature_ is renamed to _Plugin_ in the client API.
This might affect your application, as described below.

#### Imports {id="feature-plugin-imports-client"}
Update imports for [installing plugins](client-plugins.md#install):

<table>
<tr>
<td>Subsystem</td>
<td>1.6.x</td>
<td>2.0.0</td>
</tr>
<tr>
<td>
<list>
<li>
<a href="client-default-request.md">Default request</a>
</li>
<li>
<a href="client-user-agent.md">User agent</a>
</li>
<li>
<a href="client-text-and-charsets.md">Charsets</a>
</li>
<li>
<a href="client-response-validation.md">Response validation</a>
</li>
<li>
<a href="client-timeout.md">Timeout</a>
</li>
<li>
<a href="client-caching.md">HttpCache</a>
</li>
<li>
<a href="client-http-send.md">HttpSend</a>
</li>
</list>
</td>
<td><code>import io.ktor.client.features.*</code></td>
<td><code>import io.ktor.client.plugins.*</code></td>
</tr>

<tr>
<td><a href="client-auth.md">Authentication</a></td>
<td>
<code>
import io.ktor.client.features.auth.*
</code-block>
<code>
import io.ktor.client.features.auth.providers.*
</code-block>
</td>
<td>
<code>
import io.ktor.client.plugins.auth.*
</code-block>
<code>
import io.ktor.client.plugins.auth.providers.*
</code-block>
</td>
</tr>

<tr>
<td><a href="client-cookies.md">Cookies</a></td>
<td><code>import io.ktor.client.features.cookies.*</code></td>
<td><code>import io.ktor.client.plugins.cookies.*</code></td>
</tr>

<tr>
<td><a href="client-logging.md">Logging</a></td>
<td><code>import io.ktor.client.features.logging.*</code></td>
<td><code>import io.ktor.client.plugins.logging.*</code></td>
</tr>

<tr>
<td><a href="client-websockets.md">WebSockets</a></td>
<td><code>import io.ktor.client.features.websocket.*</code></td>
<td><code>import io.ktor.client.plugins.websocket.*</code></td>
</tr>

<tr>
<td><a href="client-content-encoding.md">Content encoding</a></td>
<td><code>import io.ktor.client.features.compression.*</code></td>
<td><code>import io.ktor.client.plugins.compression.*</code></td>
</tr>
</table>



#### Custom plugins {id="feature-plugin-custom-client"}
The `HttpClientFeature` interface is renamed to `HttpClientPlugin`.


### New memory model for Native targets {id="new-mm"}

With v2.0.0, using the Ktor client on [Native](client-engines.md#native) targets requires enabling the new Kotlin/Native memory model: [Enable the new MM](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#enable-the-new-mm).

> Starting with v2.2.0, the new Kotlin/Native memory model is [enabled by default](migration-to-22x.md#new-mm).


### The 'Ios' engine is renamed to 'Darwin' {id="darwin"}

Given that the `Ios` [engine](client-engines.md) targets not only iOS but other operating systems, including macOS, or tvOS, in v2.0.0, it is renamed to `Darwin`. This causes the following changes:
* The `io.ktor:ktor-client-ios` artifact is renamed to `io.ktor:ktor-client-darwin`.
* To create the `HttpClient` instance, you need to pass the `Darwin` class as an argument.
* The `IosClientEngineConfig` configuration class is renamed to `DarwinClientEngineConfig`.

To learn how to configure the `Darwin` engine, see the [](client-engines.md#darwin) section.


### WebSockets code is moved to the 'websockets' package {id="client-ws-package"}

WebSockets code is moved from `http-cio` to the `websockets` package. This requires updating imports as follows:

| 1.6.x                                 |                        2.0.0 |
|:--------------------------------------|-----------------------------:|
| `import io.ktor.http.cio.websocket.*` | `import io.ktor.websocket.*` |


### Default request {id="default-request"}

The [DefaultRequest](client-default-request.md) plugin uses a `DefaultRequestBuilder` configuration class instead of `HttpRequestBuilder`:

<tabs group="ktor_versions">
<tab title="1.6.x" group-key="1_6">

```kotlin
val client = HttpClient(CIO) {
    defaultRequest {
        // this: HttpRequestBuilder
    }
}
```

</tab>
<tab title="2.0.0" group-key="2_0">

```kotlin
val client = HttpClient(CIO) {
    defaultRequest {
        // this: DefaultRequestBuilder
    }
}
```

</tab>
</tabs>



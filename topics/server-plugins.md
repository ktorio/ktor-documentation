[//]: # (title: Server plugins)

<show-structure for="chapter" depth="2"/>

<link-summary>
Plugins provide common functionality, such as serialization, content encoding, compression, and so on.
</link-summary>

A typical request/response pipeline in Ktor looks like the following:

![Request Response Pipeline](request-response-pipeline.png){width="600"}

A request is received by the server and processed through the request pipeline. Ktor routes the request to the appropriate
handler, where your application logic processes it. The response then passes through the response pipeline before being
sent to the client.

## Add functionality with plugins {id="add_functionality"}

Many applications require functionality that is separate from the application logic. This includes authentication,
serialization and content encoding, compression, headers, cookies, and more. In Ktor, this functionality is
provided by _plugins_. 

Plugins can intercept different stages of request and response processing. They can therefore be applied at different
points in the request/response pipeline:

![Plugin pipeline](plugin-pipeline.png){width="600"}

The exact stage at which a plugin runs depends on the plugin and the hooks or pipeline phases it uses. In general, a 
request can be processed as follows:

* The routing mechanism determines which handler should process the request.
* Before being handed off to the handler, the request is processed by one or more plugins.
* The handler uses the application logic to generate a response.
* Before the response is sent to the client, the response is processed by one or more plugins.

## Routing is a plugin {id="routing"}

Plugins are designed to provide maximum flexibility, and to be present in any segment of the request/response pipeline.
In Ktor, `routing` is itself implemented as a plugin: 

![Routing as a Plugin](plugin-pipeline-routing.png){width="600"}

## Add a plugin dependency {id="dependency"}

Most plugins require a separate dependency. For example, the [`CORS`](server-cors.md) plugin requires the `ktor-server-cors`
artifact in the build script:

<var name="artifact_name" value="ktor-server-cors"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

> For a complete list of available plugins, see the [Ktor Project Generator](https://start.ktor.io/settings). For the
> dependency required by a specific plugin, see the corresponding plugin documentation.
> 
{style="tip"}

## Install plugins {id="install"}

Plugins are generally configured during the initialization phase of the server using the `install()` function. Depending
on how you [create a server](server-create-and-configure.topic), you can install a plugin inside the
`embeddedServer()` function or in an [application module](server-modules.md):

<tabs>
<tab title="embeddedServer()">

```kotlin
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.compression.*
// ...
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CORS)
        install(Compression)
        // ...
    }.start(wait = true)
}
```

</tab>
<tab title="Application.module()">

```kotlin
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.compression.*
// ...
fun Application.module() {
    install(CORS)
    install(Compression)
    // ...
}
```

</tab>
</tabs>

You can configure a plugin when you install it by passing a configuration block to the `install()` function.

For instance, when installing [`Sessions` plugin](server-sessions.md), you can configure a cookie-based session by
specifying the session type and cookie name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

### Install plugins to specific routes {id="install-route"}

In Ktor, you can install plugins not only globally, but also on specific [routes](server-routing.md). This is useful when different
resources require different plugin configurations.

For example, the following code shows installs the [`CachingHeader` plugin](server-caching-headers.md) on the `/index`
route and configures it to add caching headers:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/cachingheaders/Application.kt" include-lines="25-32"}

When the same plugin is installed at multiple levels, Ktor applies the following rules:

* Configuration of a plugin installed to a specific route overrides the corresponding [global plugin configuration](#install).
* If the same route is defined multiple times and contains multiple installations of the same plugin, routing merges the
  installations for that route. The last installation takes precedence.

In the following example both calls to `/index/a` and `/index/b` are handled by the second `CachingHeaders` installation
only:
   
 ```kotlin
 routing {
     route("index") {
         install(CachingHeaders) { /* First configuration */ }
         get("a") {
             // ...
         }
     }
     route("index") {
         install(CachingHeaders) { /* Second configuration */ }
         get("b") {
             // ...
         }
     }
 }
 ```
 {initial-collapse-state="collapsed" collapsed-title="install(CachingHeaders) { // First configuration }"}

## Default, available, and custom plugins {id="default_available_custom"}

Ktor does not install application plugins by default. Install the plugins that provide the functionality required by
your application.

Ktor provides a wide range of plugins. You can find a complete list of the available plugins in
the [Ktor Project Generator](https://start.ktor.io/settings).

You can also create your own [custom plugins](server-custom-plugins.md) to encapsulate functionality that can be reused across your
application or shared with other applications.

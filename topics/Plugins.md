[//]: # (title: Plugins)

<show-structure for="chapter" depth="2"/>

<link-summary>
Plugins provide common functionality, such as serialization, content encoding, compression, and so on.
</link-summary>

A typical request/response pipeline in Ktor looks like the following:



![Request Response Pipeline](request-response-pipeline.png){width="600"}



It starts with a request, which is routed to a specific handler, processed by our application logic, and finally responded to. 

## Add functionality with Plugins {id="add_functionality"}

Many applications require common functionality that is out of scope of the application logic. This could be things like 
serialization and content encoding, compression, headers, cookie support, etc. All of these are provided in Ktor by means of 
what we call **Plugins**. 

If we look at the previous pipeline diagram, Plugins sit between the request/response and the application logic:



![Plugin pipeline](plugin-pipeline.png){width="600"}



As a request comes in:

* It is routed to the correct handler via the routing mechanism 
* before being handed off to the handler, it goes through one or more Plugins
* the handler (application logic) handles the request
* before the response is sent to the client, it goes through one or more Plugins

## Routing is a Plugin {id="routing"}

Plugins have been designed in a way to offer maximum flexibility, and allow them to be present in any segment of the request/response pipeline.
In fact, what we've been calling `routing` until now, is nothing more than a Plugin. 



![Routing as a Plugin](plugin-pipeline-routing.png){width="600"}

## Add Plugin dependency {id="dependency"}
Most of the plugins require a specific dependency. For example, the `CORS` plugin requires adding the `ktor-server-cors` artifact in the build script:

<var name="artifact_name" value="ktor-server-cors"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install Plugins {id="install"}

Plugins are generally configured during the initialization phase of the server using the `install` function which takes a Plugin as a parameter. Depending on the way you used to [create a server](create_server.topic), you can install a plugin inside the `embeddedServer` call ...

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

... or a specified [module](Modules.md):

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

In addition to intercepting requests and responses, Plugins can have an option configuration section which is configured during this step.

For instance, when installing [Cookies](sessions.md#cookie) we can set certain parameters such as where we want cookies to be stored, or their name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

### Install Plugins to specific routes {id="install-route"}

In Ktor, you can install plugins not only globally but also to specific [routes](Routing_in_Ktor.md). This might be useful if you need different plugin configurations for different application resources. For instance, the [example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/caching-headers-routes) below shows how to add the specified [caching header](caching.md) for the `/index` route:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/cachingheaders/Application.kt" include-lines="25-32"}

Note that the following rules are applied to several installations of the same plugin:
* Configuration of a plugin installed to a specific route overrides its [global configuration](#install).
* Routing merges installations for the same route, and the last installation wins. For example, for such an application ... 
   
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
   
   ... both calls to `/index/a` and `/index/b` are handled by only second installation of the plugin.

## Default, available, and custom Plugins {id="default_available_custom"}

By default, Ktor does not activate any plugins, so it's up to you to install the plugins for the functionality your
application needs.

Ktor does, however, provide a variety of plugins that ship out of the box. You can see a complete list of these in
the [Ktor Plugin Registry](https://github.com/ktorio/ktor-plugin-registry/tree/main/plugins/server).

In addition, you can also create your own [custom plugins](custom_plugins.md).

[//]: # (title: Plugins \(formerly known as Features\))


A typical request/response pipeline in Ktor looks like the following:



![Request Response Pipeline](request-response-pipeline.png){width="600"}



It starts with a request, which is routed to a specific handler, processed by our application logic, and finally responded to. 

## Adding functionality with Plugins {id="add_functionality"}

Many applications require common functionality that is out of scope of the application logic. This could be things like 
serialization and content encoding, compression, headers, cookie support, etc. All of these are provided in Ktor by means of 
what we call **Plugins**. 

If we look at the previous pipeline diagram, Plugins sit between the request/response and the application logic:



![Plugin pipeline](feature-pipeline.png){width="600"}



As a request comes in:

* It is routed to the correct handler via the routing mechanism 
* before being handed off to the handler, it goes through one or more Plugins
* the handler (application logic) handles the request
* before the response is sent to the client, it goes through one or more Plugins

## Routing is a Plugin {id="routing"}

Plugins have been designed in a way to offer maximum flexibility, and allow them to be present in any segment of the request/response pipeline.
In fact, what we've been calling `routing` until now, is nothing more than a Plugin. 



![Routing as a Plugin](feature-pipeline-routing.png){width="600"}



## Installing Plugins {id="install"}

Plugins are generally configured during the initialization phase of the server using the `install`
function which takes a Plugin as a parameter. Depending on the way you used to [create a server](create_server.xml), you can install a plugin inside the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
    install(CORS)
    install(Compression)
    // ...
}
```

... or a specified [module](Modules.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(CORS)
    install(Compression)
    // ...
}
```

In addition to intercepting requests and responses, Plugins can have an option configuration section which is configured during this step.

For instance, when installing [Cookies](cookie_header.md) we can set certain parameters such as where we want Cookies to be stored, or their name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

## Default, available, and custom Plugins {id="default_available_custom"}

By default, Ktor does not activate any Plugin, and it's up to us as developers to install the functionality our application need.

Ktor does however provide a variety of Plugins that ship out of the box. We can see a complete list of these 
either on the [Project Generator Site](https://start.ktor.io) or in the [IntelliJ IDEA Wizard](intellij-idea.xml). In addition
we can also create our own [custom Plugins](Creating_custom_plugins.md)

For more information about sequencing of Plugins and how they intercept the request/response pipeline, see [Pipeline](Pipelines.md) in the Advanced section of the documentation. 







 




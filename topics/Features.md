[//]: # (title: Features)


A typical request/response pipeline in Ktor looks like the following:



![Request Response Pipeline](request-response-pipeline.svg)



It starts with a request, which is routed to a specific handler, processed by our application logic, and finally responded to. 

## Adding functionality with features {id="add_functionality"}

Many applications require common functionality that is out of scope of the application logic. This could be things like 
serialization and content negotiation, compression, headers, cookie support, etc. All of these are provided in Ktor by means of what we call **Features**. 

If we look at the previous pipeline diagram, features sit between the request/response and the application logic:



![Feature pipeline](feature-pipeline.svg)



As a request comes in:

* It is routed to the correct handler via the routing mechanism 
* before being handed off to the handler, it goes through one or more features
* the handler (application logic) handles the request
* before the response is sent to the client, it goes through one or more features

## Routing is a feature {id="routing"}

Features have been designed in a way to offer maximum flexibility, and allow them to be present in any segment of the request/response pipeline.
In fact, what we've been calling `routing` until now, is nothing more than a feature. 



![Routing as a Feature](feature-pipeline-routing.svg)



## Installing features {id="install"}

Features are generally configured during the initialization phase of the server using the `install`
function which takes a feature as a parameter. Depending on the way you used to [create a server](create_server.xml), you can install a feature inside the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
    install(ContentNegotiation)
    install(DefaultHeaders)
    // ...
}
```

... or a specified [module](Modules.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(ContentNegotiation)
    install(DefaultHeaders)
    // ...
}
```

In addition to intercepting requests and responses, features can have an option configuration section which is configured during this step.

For instance, when installing [Cookies](cookie_header.md) we can set certain parameters such as where we want Cookies to be stored, or their name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

### Installing features for specific routes {id="install-route"}

Features that implement `RoutingScopedFeature` can be installed in [routes](Routing_in_Ktor.md) with independent configurations:

```kotlin
routing {
    route("/no-store") {
        install(CachingHeaders) {
            options { CachingOptions(CacheControl.NoStore(CacheControl.Visibility.Private)) }
            options { CachingOptions(CacheControl.MaxAge(15)) }
        }
        // ...
    }
    route("/no-cache") {
        install(CachingHeaders) {
            options { CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Private)) }
        }
        // ...
    }
}
```

Please note that routing will merge installations from the same route and the last install will win. For example for such application
```kotlin
routing {
    route("root") {
        install(SomeFeature) { someValue = "first install" }
        get("a") {
            call.respond(call.receive<String>())
        }
    }
    route("root") {
        install(SomeFeature) { someValue = "second install" }
        get("b") {
            call.respond(call.receive<String>())
        }
    }
}
```

both calls to `root/a` and `root/b` will be handled by only second installation of the feature.

## Default, available, and custom features {id="default_available_custom"}

By default, Ktor does not activate any feature, and it's up to us as developers to install the functionality our application need.

Ktor does however provide a variety of features that ship out of the box. We can see a complete list of these 
either on the [Project Generator Site](https://start.ktor.io) or in the [IntelliJ IDEA Wizard](intellij-idea.xml). In addition, we can also create our own [custom features](Creating_custom_features.md)

For more information about sequencing of features and how they intercept the request/response pipeline, see [Pipeline](Pipelines.md) in the Advanced section of the
documentation. 







 




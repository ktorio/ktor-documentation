[//]: # (title: Features)


As mentioned in [A Ktor Application](A_Ktor_Application.md), a typical request/response pipeline in Ktor looks like the following:



![Request Response Pipeline](request-response-pipeline.svg)



It starts with a request, which is routed to a specific handler, processed by our application logic, and finally responded to. 

## Adding functionality with Features {id="add_functionality"}

Many applications require common functionality that is out of scope of the application logic. This could be things like 
serialization and content encoding, compression, headers, cookie support, etc. All of these are provided in Ktor by means of 
what we call **Features**. 

If we look at the previous pipeline diagram, Features sit between the request/response and the application logic:



![Feature pipeline](feature-pipeline.svg)



As a request comes in:

* It is routed to the correct handler via the routing mechanism 
* before being handed off to the handler, it goes through one or more Features
* the handler (application logic) handles the request
* before the response is sent to the client, it goes through one or more Features

## Routing is a Feature {id="routing"}

Features have been designed in a way to offer maximum flexibility, and allow them to be present in any segment of the request/response pipeline.
In fact, what we've been calling `routing` until now, is nothing more than a Feature. 



![Routing as a Feature](feature-pipeline-routing.svg)



## Installing Features {id="install"}

Features are generally configured during the initialization phase of the server using the `install`
function which takes a Feature as a parameter:

```kotlin
install(Routing)
install(Encoding)
```

In addition to intercepting requests and responses, Features can have an option configuration section which is configured during this step.

For instance, when installing [Cookies](working_with_cookies.md) we can set certain parameters such as where we want Cookies to be stored, or their name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

### Installing features in sub routes 

Features that implement `DynamicConfigFeature` can be installed in sub routes with independent configurations:

```kotlin
routing {
    route("/no-store") {
        install(CachingHeaders) {
            options { CachingOptions(CacheControl.NoStore(CacheControl.Visibility.Private)) }
            options { CachingOptions(CacheControl.MaxAge(15)) }
        }
        ...
    }
    route("/no-cache") {
        install(CachingHeaders) {
            options { CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Private)) }
        }
        ...
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

## Default, Available, and Custom Features {id="default_available_custom"}

By default, Ktor does not activate any Feature, and it's up to us as developers to install the functionality our application need.

Ktor does however provide a variety of Features that ship out of the box. We can see a complete list of these 
either on the [Project Generator Site](https://start.ktor.io) or in the [IntelliJ IDEA Wizard](https://plugins.jetbrains.com/plugin/10823-ktor). In addition
we can also create our own [custom Features](Creating_custom_features.md)

For more information about sequencing of Features and how they intercept the request/response pipeline, see [Pipeline](Pipelines.md) in the Advanced section of the
documentation. 







 




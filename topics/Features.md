[//]: # (title: Features)


As mentioned in the previous section, a typical request/response pipeline in Ktor looks like the following:

![Request Response Pipeline](request-response-pipeline.svg)

It starts with a request, which is routed to a specific handler, processed by our application logic, and finally responded to. 

## Adding functionality with Features

Many applications require common functionality that is out of scope of the application logic. This could be things like 
serialization and content encoding, compression, headers, cookie support, etc. All of these are provided in Ktor by means of 
what we call **Features**. 

If we look at the previous pipeline diagram, Features sit between the request/response and the application logic:

![Feature pipeline](feature-pipeline.svg)

As a request comes in:

* It is routed to the correct handler via the routing mechanism 
* Before being handed off to the handler, it goes through one or more Features
* The handler (application logic) handles the request
* Before the response is send to the client, it goes through one or more Features

## Routing is a Feature

Features have been designed in a way to offer maximum flexibility, and allow them to be present in any segment of the request/response pipeline.
In fact, what we've been calling `routing` until now, is nothing more than a Feature. 

![Routing as a Feature](feature-pipeline-routing.svg)

## Installing Features

Features are generally configured during the initialization phase of the server using the `install`
function which takes a Feature as a parameter:

```kotlin
install(Routing)
install(ContentNegotiation)
```

In addition to intercepting requests and responses, Features can have an option configuration section which is configured during this step.

For instance, when installing [Cookies](working_with_cookies.md) we can set certain parameters such as where we want Cookies to be stored, or their name:

```kotlin
install(Sessions) {
    cookie<MyCookie>("MY_COOKIE")
} 
```

## Default, Available, and Custom Features

By default, Ktor does not activate any Feature, and it's up to us as developers to install the functionality our application need.

Ktor does however provide a variety of Features that ship out of the box. We can see a complete list of these 
either on the [Project Generator Site](https://start.ktor.io) or in the [IntelliJ IDEA Wizard](https://plugins.jetbrains.com/plugin/10823-ktor). In addition
we can also create our own [custom Features](Creating_custom_features.md)

For more information about sequencing of Features and how they intercept the request/response pipeline, see [Pipeline](Pipelines.md) in the Advanced section of the
documentation. 







 




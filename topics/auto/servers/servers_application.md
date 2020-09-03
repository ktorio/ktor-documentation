[//]: # (title: Application)
[//]: # (category: servers)
[//]: # (permalink: /servers/application.html)
[//]: # (caption: What is an Application?)
[//]: # (ktor_version_review: 1.2.1)

A Ktor Server Application is a custom program listening to one or more ports using a [configured server engine](/servers/configuration.html),
composed by [modules](#modules) with the application logic, that install [features](#features), like [routing](/servers/features/routing.html),
[sessions](/servers/features/sessions.html), [compression](/servers/features/compression.html), etc. to handle HTTP/S 1.x/2.x and WebSocket requests.

>Ktor also provides functionality to [handle raw sockets](/servers/raw-sockets.html), but not as part of the Application and
>its pipeline.
>
{type="note"}

## Application

An `Application` instance is the main unit of a Ktor Application. When a request comes in
(a request can be HTTP, HTTP/2 or WebSocket requests), it is converted to an `ApplicationCall`
and goes through a pipeline which is owned by the `Application`. The pipeline consists of one or more
interceptors that are previously installed, providing certain functionality such as routing,
compression, etc. that ends handling the request.

Normally, a Ktor program configures the Application pipeline through [modules](#modules)
that [install and configure features](#features).

>You can read further information about the pipeline, in the [lifecycle](/servers/lifecycle.html) section.
>
{type="note"}

## ApplicationCall

Check the [dedicated page about ApplicationCall](/servers/calls.html).

## Features

A feature is a singleton (usually a companion object) that you can install and configure for a pipeline.
Ktor includes some standard features, but you can add your own or other features from the community. 
You can install features in any pipeline, like the application itself, or specific routes.

>You can read more about [features](/servers/features.html) in its dedicated page.
>
{type="note"}

## Modules
{id="modules"}

A Ktor module is just a user-defined function receiving the `Application` class that is in charge of configuring
the server pipeline, install features, registering routes, handling requests, etc.

You have to specify the modules to load when the server starts in [the `application.conf` file](/servers/configuration.html#hocon-file).

A simple module function would look like this:


```kotlin
package com.example.myapp

fun Application.mymodule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

Of course, you can split the module function in several smaller functions or classes.

Modules are referenced by their fully qualified name: the fully qualified name of the class and the method name,
separated by a dot (`.`).

So for the example, the module's fully qualified name would be:

```
com.example.myapp.MainKt.mymodule
```

>`mymodule` is an extension method of the class `Application` (where `Application` is the *receiver*).
>Since it is defined as a top-level function, Kotlin creates a JVM class with a `Kt` suffix (`FileNameKt`),
>and adds the extension method as a static method with the receiver as its first parameter.
>In this case, the class name is `MainKt` in the `com.example.myapp` package, and the Java method signature would be
>`static public void mymodule(Application app)`.
>
{type="note"}

## What's next

- [Application calls](/servers/calls.html)
- [Application lifecycle explanation](/servers/lifecycle.html)
- [Application configuration](/servers/configuration.html)
- [Pipelines explained](/advanced/pipeline)
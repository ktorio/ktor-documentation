[//]: # (title: Structure)
[//]: # (caption: Building Complex Servers)
[//]: # (category: servers)
[//]: # (permalink: /servers/structure.html)
[//]: # (keywords: routing, routes, structuring, growing, dependency injection, guice, external configuration,)

Depending on the complexity of the code of your server, you might want to structure your code
one way or another. This page proposes some strategies to structure your code according to its
complexity, adapting to its growth, while keeping it as simple as possible.

**Table of contents:**

* TOC

## Hello World

To get started with Ktor, you can start with an `embeddedServer` in a simple `main` function.

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }.start(wait = true)
}
```

This works fine to understand how Ktor works and to have all the application code available
at a glance.

## Defining modules

You can extract the code configuring the server, also called a Ktor module, to an extension method:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, module = Application::mainModule).start(wait = true)
}

fun Application.mainModule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

## Extracting routes

Once your code starts to grow, and you have more routes defined, you will probably want to split
the code up instead of growing your main function indefinitely.

A simple way to do this, is to extract routes into extension methods using the `Routing` class as a receiver.

Depending on the size, maybe still keeping it in the same file or you can move it to other files:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, module = Application::mainModule).start(wait = true)
}

fun Application.mainModule() {
    routing {
        root()
    }
}

// Extracted route
fun Routing.root() {
    get("/") {
        call.respondText("Hello World!")
    }
}
```

Inside the `routing { ... }` block there is an implicit `this: Routing`, you can call the `root` method directly,
it is effectively like calling `this.root()`.
{ .note}

## Deployment and `application.conf`

Once you want to deploy your server, you might also want to provide or to change the configuration of the server
externally without recompiling it.

Ktor libraries expose some entrypoints that read an `application.conf` file from the resources, or by an external
file. In this file you can define things like the entry point of the application, the port used, the ssl configuration
or arbitrary configurations.

You can read more about using `application.conf` in the [configuration page](/servers/configuration.html).

{:comment}
## Dependency injection using Guice

Ktor doesn't impose any dependency injection system. In fact, you can easily write even big applications
without using any.
{/comment}

## Health checks

Depending on your application, you might want to use different ways to create a health check.
The easiest way would be to enable an endpoint like `/health_check` that returns
something like HTTP 200 `OK`, while optionally verifying your dependant services.
It's completely up to you.

You can also use the [StatusPages feature](/servers/features/status-pages.html) to handle exceptions.

```kotlin
install(StatusPages){
    exception<Throwable> { cause ->
        call.respond(HttpStatusCode.InternalServerError)
    }
}
routing {
    get("/health_check") {
        // Check databases/other services.
        call.respondText("OK")
    }
}
```
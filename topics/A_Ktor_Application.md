[//]: # (title: A Ktor Application)

Ktor can be used to create a variety of server (and client-side) applications. Whether we want to create a website that serves
static and dynamic pages, an HTTP endpoint, a RESTful system, or even microservices, Ktor makes it all possible.

In this section we're going to cover the basics of what a Ktor Server Application is and how the pieces all fit together. For more advanced
topics and to dive deeper into Ktor under the covers, see the [](How_Ktor_works.md) topic.

## The Simplest Ktor Application

One of Ktor's goals is to remain as simple as possible and to avoid any kind of *magic*. The equivalent to a *Hello World* application in Ktor would
be:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, Ktor!")
            }
        }
    }.start(true)
}
```

We start by creating an server, in this case using Netty as an [Engine](Engines.md), and setting the port to 8080. After this, we define
a route to respond to requests made to `/` with a simple `Hello, Ktor!` text. Finally we start the server. 

In real-world applications, we would most likely [organise things a bit differently](Modules.md) so that the code
remains maintainable as the application grows. In the following sections we'll dive deeper into
how Ktor works and explain some of the concepts we've used. 

## The Request and Response Pipeline

The request and response flow for a Ktor application is represented by the following diagram:



![Request and response pipeline](request-response-pipeline.svg)



When a request comes in, it is processed by the routing mechanism, which redirects it to the route handler. This route handle consists 
of our application logic which could be anything from responding with a simple text (like the example above), to processing the input, storing
it in a database, etc. Finally, once everything is done, the handler should respond to the request. 

In the next section we'll look [Plugins](Plugins.md) which is core to all Ktor applications, and provides much of the functionality, 
including routing! 










[//]: # (title: Calls)
[//]: # (caption: ApplicationCall)
[//]: # (keywords: calls requests responses)
[//]: # (category: servers)
[//]: # (permalink: /servers/calls.html)
[//]: # (children: /servers/calls/)
[//]: # (ktor_version_review: 1.0.0)

When handling routes, or directly intercepting the pipeline, you get a context with an ApplicationCall.

The `ApplicationCall` provides access to two main properties [`ApplicationRequest`](/servers/calls/requests.html) and [`ApplicationResponse`](/servers/calls/responses.html).
As their names indicate, they correspond to the incoming request and outgoing response. In addition to these,
it also provides an `ApplicationEnvironment`, and some useful functions to help respond to client requests.
Given that pipelines can be executed asynchronously, `ApplicationCall` also represents the logical execution
context with `Attributes` to pass data between various parts of the pipeline.

Installing an interceptor into the pipeline is the primary method to alter the processing of an `ApplicationCall`.
Nearly all Ktor [features](/servers/features) are interceptors that perform various operations in different phases of
the application call processing. 

```kotlin
    intercept(ApplicationCallPipeline.Call) { 
        if (call.request.uri == "/")
            call.respondText("Test String")
    }
```
The code above installs an interceptor into the `Call` phase of an `ApplicationCall` processing and responds with plain text
when the request is asking for a root page.  

This is just an example, and usually, page requests are not handled in this way, as there is a [routing](/servers/features/routing) facility that does this
 and more. Also, as mentioned previously, defining interceptor is usually done using [features](/servers/features) with an `install` function.
   
Most functions available on `ApplicationCall` (such as `respondText` above) are `suspend` functions, indicating that they 
can potentially execute asynchronously.
 
See advanced topic [Pipeline](/advanced/pipeline) for more information on the mechanics of processing `ApplicationCall`s.

## What's next

- [Application lifecycle explanation](https://ktor.io/servers/lifecycle.html)
- [Application configuration](https://ktor.io/servers/configuration.html)
- [Pipelines explained](/advanced/pipeline)
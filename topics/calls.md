[//]: # (title: Handling Requests and Responses)


Ktor allows you to handle incoming requests and send responses inside [route handlers](Routing_in_Ktor.md#define_route).
Access to [ApplicationCall](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/index.html)
* For a request, you can get various information, such as headers, cookies
* 

```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```


When handling routes, or directly intercepting the pipeline, you get a context with an ApplicationCall.

The `ApplicationCall` provides access to two main properties [`ApplicationRequest`](requests.md) and [`ApplicationResponse`](responses.md).
As their names indicate, they correspond to the incoming request and outgoing response. In addition to these,
it also provides an `ApplicationEnvironment`, and some useful functions to help respond to client requests.
Given that pipelines can be executed asynchronously, `ApplicationCall` also represents the logical execution
context with `Attributes` to pass data between various parts of the pipeline.

Installing an interceptor into the pipeline is the primary method to alter the processing of an `ApplicationCall`.
Nearly all Ktor [features](Features.md) are interceptors that perform various operations in different phases of
the application call processing. 

```kotlin
    intercept(ApplicationCallPipeline.Call) { 
        if (call.request.uri == "/")
            call.respondText("Test String")
    }
```
The code above installs an interceptor into the `Call` phase of an `ApplicationCall` processing and responds with plain text
when the request is asking for a root page.  

This is just an example, and usually, page requests are not handled in this way, as there is a [routing](Routing_in_Ktor.md) facility that does this
 and more. Also, as mentioned previously, defining interceptor is usually done using [features](Features.md) with an `install` function.
   
Most functions available on `ApplicationCall` (such as `respondText` above) are `suspend` functions, indicating that they 
can potentially execute asynchronously.
 
See advanced topic [](Pipelines.md) for more information on the mechanics of processing `ApplicationCall`s.

## What's next

- [Application configuration](Configurations.xml)
- [Pipelines explained](Pipelines.md)
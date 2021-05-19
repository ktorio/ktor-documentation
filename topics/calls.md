[//]: # (title: Handling Requests and Responses)

Ktor allows you to handle incoming [requests](requests.md) and send [responses](responses.md) inside [route handlers](Routing_in_Ktor.md#define_route). Inside a route handler, you can get access to a request and response using [ApplicationCall](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/index.html).
```kotlin
routing {
    get("/") {
        val uri = call.request.uri
        call.respondText("Request uri: $uri")
    }
}
```

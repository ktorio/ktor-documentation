[//]: # (title: Routing in Ktor)


Routing is at the heart of a Ktor application. It's what allows incoming requests to be handled. When a request such as `/hello` 
is made, the routing mechanism in Ktor allows us to define how we want this to request to be served. 

This is accomplished using the `Routing` Feature that can be installed in any Ktor server application:

```Kotlin
install(Routing) {
    route("/hello", HttpMethod.Get) {
        handle {
            call.respondText("Hello")
        }
    }
}
```

`route` takes three parameters:

* The URL [pattern](#url-patterns.md)
* The Verb, which can be `GET`, `POST`, `PUT`, `DELETE`, `HEAD`, `OPTION`, or `PATCH`
* The Handler, which provides us with access to handling the request 

For convenience, Ktor provides a series of functions that make route definitions much easier and more concise. The previous code could be expressed as:

```kotlin
install(Routing) {
    get("/hello") {
        call.respondText("Hello")
    }
}
```

where we can see that the `route` function is replaced with a `get` function that now only needs to take the URL and the code to handle the request. In a similar 
way Ktor provides functions for all the other verbs, that is `put`, `post`, `head`, and so on.

# Routing Function
 
Similar to how the `get` function simplifies defining routes, installing routes is also made easier by using the `routing` function. This means that
instead of having to use `install(Routing)` we can simply use the function `routing`:

<note>
    <p>The {id} part of the path is how we define route parmeters in Ktor, which is covered in detail in <a href="route_parameters.md">Route Parameters</a></p>
</note>

```kotlin
routing {
    get("/customer/{id}") {

    }
    post("/customer") {

    }
    get("/order/{id}") {
    
    }
}
```

In this case, each route has its own function and responds to the specific endpoint and HTTP verb.

An alternative way is to group these by paths, whereby we define the path and then place the verbs for that path as nested functions: 

```kotlin
routing {
    route("/customer") {
        get {

        }

        post {

        }
    }
}
```

Independently of how we do the grouping, Ktor also allows us to have sub-routes. The following example shows us how to respond to incoming requests to `/order/shipment`:

```kotlin
routing {
    route("/order") {
        route("/shipment") {
            get {
                
            }
            post {
                
            }
        }
    }
}
```

# Using Route Extension Functions

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs, and 
remove clutter of having all routes in a single routing function. We can apply this pattern independently of how we decide 
to group routes. As such, the first example could be represented in a cleaner way:

```kotlin
routing {
    customerByIdRoute()
    createCustomerRoute()
    orderByIdRoute()
    createOrder()
}

fun Route.customerByIdRoute() {
    get("/customer/{id}") {

    }
}

fun Route.createCustomerRoute() {
    post("/customer") {

    }
}

fun Route.orderByIdRoute() {
    get("/order/{id}") {
    
    }
}

fun Route.createOrder() {
    post("/order") {

    }
}
```

For more advanced topics around routing please see [Advanced Routing].

 














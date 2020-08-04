[//]: # (title: Routing in Ktor)


Routing is at the heart of a Ktor application. It's what allows incoming requests to be handled. When a request such as `/hello` 
is made, the routing mechanism in Ktor allows us to define how we want this to request to be served. 

This is accomplished using the `Routing` [Feature](Features.md) which can be installed in any Ktor server application:

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

* The URL pattern
* The Verb, which can be `GET`, `POST`, `PUT`, `DELETE`, `HEAD`, `OPTION`, or `PATCH`
* The Handler, which provides us with access to handling the request 

Given the Routing Feature is so common in any application, there is a convenient `routing` function that makes it simpler to install routing:

```kotlin
routing {
    route("/hello", HttpMethod.Get) {
        handle {
            call.respondText("Hello")
        }
    }
}
```

We can see that we've replaced `install(Routing)` with the `routing` function.

## Verbs as functions

Similar to how `routing` simplifies usage of the Routing Feature, Ktor provides a series of functions that make defining route handlers much easier and more concise. 
The previous code can be expressed as:

```kotlin
install(Routing) {
    get("/hello") {
        call.respondText("Hello")
    }
}
```

We can see that the `route` function is replaced with a `get` function that now only needs to take the URL and the code to handle the request. In a similar 
way Ktor provides functions for all the other verbs, that is `put`, `post`, `head`, and so on.

## Defining multiple route handlers

If we want to define multiple route handlers, which of course is the case for any application, we can just add them to the `routing` function:


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

<note>
    <p>The {id} part of the path is how we define route parmeters in Ktor, which is covered in detail in <a href="route_parameters.md">Route Parameters</a></p>
</note>

In this case, each route has its own function and responds to the specific endpoint and HTTP verb.

An alternative way is to group these by paths, whereby we define the path and then place the verbs for that path as nested functions, using the `route` function: 

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

Independently of how we do the grouping, Ktor also allows us to have sub-routes as parameters to `route` functions. The following example shows us how to respond to incoming requests to `/order/shipment`:

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

## Using Route Extension Functions

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs and 
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

For our application to scale when it comes to maintainability, it is recommended to follow certain [Structuring patterns](Structuring_Applications.md).

<note>
<p>
For more advanced topics around routing please see [Advanced Routing].
</p>
</note>


 














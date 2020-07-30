[//]: # (title: Routing Options )

One of Ktor’s strong points is in the flexibility it offers in terms of structuring our application. Different to many other server-side frameworks, it doesn’t force us into a specific pattern such as having to place all cohesive routes in a single class name `CustomerController` for instance. While it is certainly possible, it's not required.

In this section we're going to examine the different routing options we have with Ktor, not only how we can define these, but also group and structure our applications.
 
# Defining routes

Generally routes are defined using the `routing` function. We can then create routes using verbs and paths, with each verb and path being its own route.

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

An alternative way is to group these by paths, whereby we define the path and then place the verbs for that path as nested functions. 

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

Independently of how we do the grouping, Ktor also allows us to have sub-routes. The following example shows us how to respond to incoming requests to `/order/shipment`.

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

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs, and 
remove clutter of having all routes in a single routing function. We can apply this pattern independently of how we decide 
to group routes. As such, the first example could be represented in a cleaner way

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

# Grouping 

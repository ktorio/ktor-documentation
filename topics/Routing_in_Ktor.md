[//]: # (title: Routing)

Routing is the core Ktor [feature](Features.md) for handling incoming requests in a server application. When the client makes a request to a specific URL (for example, `/hello`), the routing mechanism allows us to define how we want this request to be served. 

## Install Routing {id="install_feature"}

The Routing feature can be installed in the following way:

```Kotlin
install(Routing) {
    // ...
}
```

Given the `Routing` feature is so common in any application, there is a convenient `routing` function that makes it simpler to install routing. In the code snippet below, we've replaced `install(Routing)` with the `routing` function:

```kotlin
routing {
    // ...
}
```

## Build a route {id="build_route"}

After [installing](#install_feature) the Routing feature, you can call the `route` function inside `routing` to build a route:
```kotlin
routing {
  route("/hello", HttpMethod.Get) {
    handle {
      call.respondText("Hello")
    }
  }
}
```

The `route` function takes three parameters: 

* **URL pattern**  
Used to [match a URL path](#match_url), for example, `/hello`, `/customer/{id}`.
  
* **HTTP verb**  
  Specifies the HTTP verb, such as `GET`, `POST`, `PUT`, and so on. You can pass the required verb as the `HttpMethod` class value. As an alternative to using the `route` function with the specified verb, you can call a dedicated [verb function](#verbs) such as `get`, `post`, and so on.  
   > The HTTP verb parameter value is optional. You can use the `route` function without specifying the HTTP verb to group [route handlers](#multiple_routes) and define nested routes.
* **Handler**  
  Provides us with access to handling the request. Inside the handler, you can get access to the [ApplicationCall](calls.md) and obtain URL and query parameters.

### Verbs as functions {id="verbs"}

Similar to how `routing` simplifies usage of the Routing feature, Ktor provides a series of functions that make defining route handlers much easier and more concise. The previous code can be expressed as:

```kotlin
routing {
    get("/hello") {
        call.respondText("Hello")
    }
}
```

We can see that the `route` function is replaced with a `get` function that now only needs to take the URL and the code to handle the request. Similarly, Ktor provides functions for all the other verbs, that is `put`, `post`, `head`, and so on.


## Define a URL pattern {id="match_url"}

The URL pattern passed to the [`route`](#build_route) or [dedicated verb](#verbs) function is used to match a _path_ component of the URL, for instance, `/hello` or `/order/shipment`. A path can contain a sequence of path segments separated by a slash `/` character. Note that Ktor distinguishes paths with and without a trailing slash.

Ktor matches URL paths using the following rules:

* `*` (_wildcard_) matches any path segment and can't be missing.
  
  _Example_: `/user/*` matches `/user/john`, but doesn't match `/user`.
* `{...}` (_tailcard_) matches all the rest of the URL path and can include several path segments. Can be empty.
  
  _Example_: `/user/{...}` matches `/user/john/settings` as well as `/user`.
* `{param}` matches a path segment and captures it as a parameter named `param`. This path segment is mandatory, but you can make it optional by adding a question mark: `{param?}`.
  
  _Example 1_: `/user/{login}` matches `/user/john`, but doesn't match `/user`.   
  _Example 2_: `/user/{login?}` matches `/user/john` as well as `/user`.
  
  To access a parameter value inside the route handler, use the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _john_ for the `/user/john` path:
   ```kotlin
   get("/user/{login}") {
     call.respondText("Hello, ${call.parameters["login"]}")
   }
   ```
* `{param...}` matches all the rest of the URL path and puts multiple values for each path segment into parameters using `param` as key.

  _Example_: `/user/{param...}` matches `/user/john/settings`.

  To access path segments' values inside the route handler, use `call.parameters.getAll("param")`. For the example above, the `getAll` function will return an array containing the _john_ and _settings_ values.

> To learn how to access URL query parameters inside the handler, see [](requests.md#get).


## Define multiple route handlers {id="multiple_routes"}

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

[comment]: <> (<note>)

[comment]: <> (    <p>The {id} part of the path is how we define route parameters in Ktor, which is covered in detail in <a href="route_parameters.md">Route Parameters</a></p>)

[comment]: <> (</note>)

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

## Route extension functions {id="route_extension_function"}

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs and remove clutter of having all routes in a single routing function. We can apply this pattern independently of how we decide to group routes. As such, the first example could be represented in a cleaner way:

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





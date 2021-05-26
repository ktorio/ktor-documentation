[//]: # (title: Routing)

Routing is the core Ktor [plugin](Plugins.md) for handling incoming requests in a server application. When the client makes a request to a specific URL (for example, `/hello`), the routing mechanism allows us to define how we want this request to be served. 

## Install Routing {id="install_feature"}

The Routing plugin can be installed in the following way:

```Kotlin
import io.ktor.routing.*

install(Routing) {
    // ...
}
```

Given the `Routing` plugin is so common in any application, there is a convenient `routing` function that makes it simpler to install routing. In the code snippet below, `install(Routing)` is replaced with the `routing` function:

```kotlin
import io.ktor.routing.*

routing {
    // ...
}
```

## Define a route handler {id="define_route"}

After [installing](#install_feature) the Routing plugin, you can call the [route](https://api.ktor.io/%ktor_version%/io.ktor.routing/route.html) function inside `routing` to define a route:
```kotlin
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.response.*

routing {
    route("/hello", HttpMethod.Get) {
        handle {
            call.respondText("Hello")
        }
    }
}
```

Ktor also provides a series of functions that make defining route handlers much easier and more concise. For example, you can replace the previous code with a [get](https://api.ktor.io/%ktor_version%/io.ktor.routing/get.html) function that now only needs to take the URL and the code to handle the request:

```kotlin
import io.ktor.routing.*
import io.ktor.response.*

routing {
    get("/hello") {
        call.respondText("Hello")
    }
}
```
Similarly, Ktor provides functions for all the other verbs, that is `put`, `post`, `head`, and so on.

In summary, you need to specify the following settings to define a route:

* **HTTP verb**  
  Choose the HTTP verb, such as `GET`, `POST`, `PUT`, and so on. The most convenient way is to use a dedicated verb function, such as `get`, `post`, `put`, and so on.

* **Path pattern**  
  Specify a path pattern used to [match a URL path](#match_url), for example, `/hello`, `/customer/{id}`. You can pass a path pattern right to the `get`/`post`/etc. function, or you can use the `route` function to group [route handlers](#multiple_routes) and define nested routes.
  
* **Handler**  
  Specify how to handle [requests](requests.md) and [responses](responses.md). Inside the handler, you can get access to `ApplicationCall`, handle client requests, and send responses.



## Specify a path pattern {id="match_url"}

A path pattern passed to the [routing](#define_route) functions (`route`, `get`, `post`, etc.) is used to match a _path_ component of the URL. A path can contain a sequence of path segments separated by a slash `/` character.

> Note that Ktor distinguishes paths with and without a trailing slash. You can change this behaviour by [installing](Features.md#install) the `IgnoreTrailingSlash` feature.

Below are several path examples:
* `/hello`  
  A path containing a single path segment.
* `/order/shipment`  
  A path containing several path segments. You can pass such a path to the [route/get/etc.](#define_route) function as is or organize sub-routes by [nesting](#multiple_routes) several `route` functions.
* `/user/{login}`  
  A path with the `login` [route parameter](#route_parameter), whose value can be accessed inside the route handler.
* `/user/*`  
  A path with a [wildcard character](#wildcard) that matches any path segment.
* `/user/{...}`  
  A path with a [tailcard](#tailcard) that matches all the rest of the URL path.
* `/user/{param...}`  
  A path containing a [route parameter with tailcard](#route_parameter_tailcard).


### Wildcard {id="wildcard"}
A _wildcard_ (`*`) matches any path segment and can't be missing. For example, `/user/*` matches `/user/john`, but doesn't match `/user`.

### Tailcard {id="tailcard"}
A _tailcard_ (`{...}`) matches all the rest of the URL path, can include several path segments, and can be empty. For example, `/user/{...}` matches `/user/john/settings` as well as `/user`.

### Route parameter {id="route_parameter"}
A _route parameter_ (`{param}`) matches a path segment and captures it as a parameter named `param`. This path segment is mandatory, but you can make it optional by adding a question mark: `{param?}`. For example:
* `/user/{login}` matches `/user/john`, but doesn't match `/user`.
* `/user/{login?}` matches `/user/john` as well as `/user`.

To access a parameter value inside the route handler, use the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _admin_ for the `/user/admin` path:
```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}

> To learn how to access URL query parameters inside the handler, see [](requests.md#query_parameters).

### Route parameter with tailcard {id="route_parameter_tailcard"}

A route parameter with a tailcard (`{param...}`) matches all the rest of the URL path and puts multiple values for each path segment into parameters using `param` as key. For example, `/user/{param...}` matches `/user/john/settings`.  
To access path segments' values inside the route handler, use `call.parameters.getAll("param")`. For the example above, the `getAll` function will return an array containing the _john_ and _settings_ values.

## Define multiple route handlers {id="multiple_routes"}

If you want to define multiple route handlers, which of course is the case for any application, you can just add them to the `routing` function:

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

An alternative way is to group these by paths, whereby you define the path and then place the verbs for that path as nested functions, using the `route` function:

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

Independently of how you do the grouping, Ktor also allows you to have sub-routes as parameters to `route` functions. The following example shows us how to respond to incoming requests to `/order/shipment`:

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

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs and remove clutter of having all routes in a single routing function. You can apply this pattern independently of how you decide to group routes. As such, the first example could be represented in a cleaner way:

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





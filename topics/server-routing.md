[//]: # (title: Routing)

<show-structure for="chapter" depth="2"/>

<link-summary>
Routing is a core plugin for handling incoming requests in a server application.
</link-summary>

Routing is the core Ktor [plugin](server-plugins.md) for handling incoming requests in a server application. When the client makes a request to a specific URL (for example, `/hello`), the routing mechanism allows us to define how we want this request to be served. 

## Install Routing {id="install_plugin"}

The Routing plugin can be installed in the following way:

```Kotlin
import io.ktor.server.routing.*

install(Routing) {
    // ...
}
```

Given the `Routing` plugin is so common in any application, there is a convenient `routing` function that makes it simpler to install routing. In the code snippet below, `install(Routing)` is replaced with the `routing` function:

```kotlin
import io.ktor.server.routing.*

routing {
    // ...
}
```

## Define a route handler {id="define_route"}

After [installing](#install_plugin) the Routing plugin, you can call the [route](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/route.html) function inside `routing` to define a route:
```kotlin
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*

routing {
    route("/hello", HttpMethod.Get) {
        handle {
            call.respondText("Hello")
        }
    }
}
```

Ktor also provides a series of functions that make defining route handlers much easier and more concise. For example, you can replace the previous code with a [get](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/get.html) function that now only needs to take the URL and the code to handle the request:

```kotlin
import io.ktor.server.routing.*
import io.ktor.server.response.*

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

  Specify a path pattern used to [match a URL path](#match_url), for example, `/hello`, `/customer/{id}`. You can pass a path pattern right to the `get`/`post`/etc. function, or you can use the `route` function to group [route handlers](#multiple_routes) and define [nested routes](#nested_routes).
  
* **Handler**

  Specify how to handle [requests](server-requests.md) and [responses](server-responses.md). Inside the handler, you can get access to `ApplicationCall`, handle client requests, and send responses.



## Specify a path pattern {id="match_url"}

A path pattern passed to the [routing](#define_route) functions (`route`, `get`, `post`, etc.) is used to match a _path_ component of the URL. A path can contain a sequence of path segments separated by a slash `/` character.

> Note that Ktor distinguishes paths with and without a trailing slash. You can change this behavior by [installing](server-plugins.md#install) the `IgnoreTrailingSlash` plugin.

Below are several path examples:
* `/hello`  
  A path containing a single path segment.
* `/order/shipment`  
  A path containing several path segments. You can pass such a path to the [route/get/etc.](#define_route) function as is or organize sub-routes by [nesting](#multiple_routes) several `route` functions.
* `/user/{login}`  
  A path with the `login` [path parameter](#path_parameter), whose value can be accessed inside the route handler.
* `/user/*`  
  A path with a [wildcard character](#wildcard) that matches any path segment.
* `/user/{...}`  
  A path with a [tailcard](#tailcard) that matches all the rest of the URL path.
* `/user/{param...}`  
  A path containing a [path parameter with tailcard](#path_parameter_tailcard).
* `Regex("/.+/hello")`  
  A path containing a [regular expression](#regular_expression) that matches path segments up to and including the last occurrence of the `/hello`.


### Wildcard {id="wildcard"}
A _wildcard_ (`*`) matches any path segment and can't be missing. For example, `/user/*` matches `/user/john`, but doesn't match `/user`.

### Tailcard {id="tailcard"}
A _tailcard_ (`{...}`) matches all the rest of the URL path, can include several path segments, and can be empty. For example, `/user/{...}` matches `/user/john/settings` as well as `/user`.

### Path parameter {id="path_parameter"}
A _path parameter_ (`{param}`) matches a path segment and captures it as a parameter named `param`. This path segment is mandatory, but you can make it optional by adding a question mark: `{param?}`. For example:
* `/user/{login}` matches `/user/john`, but doesn't match `/user`.
* `/user/{login?}` matches `/user/john` as well as `/user`.
   > Note that optional path parameters `{param?}` can only be used at the end of the path.
   >
   {type="note"}

To access a parameter value inside the route handler, use the `call.parameters` property. For example, `call.parameters["login"]` in the code snippet below will return _admin_ for the `/user/admin` path:
```kotlin
```
{src="snippets/_misc/RouteParameter.kt"}

> If a request contains a query string, `call.parameters` also includes parameters of this query string. To learn how to access a query string and its parameters inside the handler, see [](server-requests.md#query_parameters).

### Path parameter with tailcard {id="path_parameter_tailcard"}

A path parameter with a tailcard (`{param...}`) matches all the rest of the URL path and puts multiple values for each path segment into parameters using `param` as a key. For example, `/user/{param...}` matches `/user/john/settings`.
To access path segments' values inside the route handler, use `call.parameters.getAll("param")`. For the example above, the `getAll` function will return an array containing the _john_ and _settings_ values.

### Regular expression {id="regular_expression"}

Regular expressions can be used with all defining route handlers functions: `route`, `get`, `post`, and so on. 

> To know more about regular expressions, see [Kotlin documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/).

Let's write a route that matches any path that ends with `/hello`.

```kotlin
import io.ktor.server.routing.*
import io.ktor.server.response.*

routing {
    get(Regex(".+/hello")) {
        call.respondText("Hello")
    }
}
```
With this route definition, any incoming request to a path ending with `/hello`, such as `/foo/hello`, `/bar/baz/hello`, 
and so on, will be matched.

#### Accessing path parts in handler

In regular expressions, named groups are a way to capture a specific part of a string that matches a pattern and assign it a name.
The syntax `(?<name>pattern)` is used to define named groups, where `name` is the name of a group and 
`pattern` is a regular expression pattern that matches the group.

By defining a named group in a route function, you can capture a part of the path, and then in the handler function,
you can access the captured parameter using the `call.parameters` object.

For example, you can define a route that matches requests to a path that includes an integer identifier followed by `/hello`.

```kotlin
import io.ktor.server.routing.*
import io.ktor.server.response.*

routing {
    get(Regex("""(?<id>\d+)/hello""")) {
        val id = call.parameters["id"]!!
        call.respondText(id)
    }
}
```
In the code below, the `(?<id>\d+)` named group is used to capture the integer identifier `id` from a requested path,
and the `call.parameters` property is used to access the captured `id` parameter in the handler function.

Unnamed groups can't be accessed inside a regex route handler, but you can use them to match the path. For example, path `hello/world` will be matched while `hello/World` not:

```kotlin
import io.ktor.server.routing.*
import io.ktor.server.response.*

routing {
    get(Regex("hello/([a-z]+)")) {
        call.respondText("Hello")
    }
}
```
Also, the whole path segment needs to be consumed by regex. For example, path pattern `get(Regex("[a-z]+"))` will not match the path `"hello1"` 
but will match the part `hello` of the path `hello/1` and leave `/1` for the next route.

## Define multiple route handlers {id="multiple_routes"}

### Group routes by verb functions {id="group_by_verb"}

If you want to define multiple route handlers, which of course is the case for any application, you can just add them to the `routing` function:

```kotlin
routing {
    get("/customer/{id}") {

    }
    post("/customer") {

    }
    get("/order") {

    }
    get("/order/{id}") {
    
    }
}
```

In this case, each route has its own function and responds to the specific endpoint and HTTP verb.

### Group routes by paths {id="group_by_path"}

An alternative way is to group these by paths, whereby you define the path and then place the verbs for that path as nested functions, using the `route` function:

```kotlin
routing {
    route("/customer") {
        get {

        }
        post {

        }
    }
    route("/order") {
        get {

        }
        get("/{id}") {

        }
    }
}
```

### Nested routes {id="nested_routes"}

Independently of how you do the grouping, Ktor also allows you to have sub-routes as parameters to `route` functions. 
This can be useful to define resources that are logically children of other resources.
The following example shows us how to respond to `GET` and `POST` requests to `/order/shipment`:

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

So, each `route` call generates a separate path segment.


A path pattern passed to the [routing](#define_route) functions (`route`, `get`, `post`, etc.) is used to match a _path_ component of the URL. A path can contain a sequence of path segments separated by a slash `/` character.

## Route extension functions {id="route_extension_function"}

A common pattern is to use extension functions on the `Route` type to define the actual routes, allowing us easy access to the verbs and remove clutter of having all routes in a single routing function. You can apply this pattern independently of how you decide to group routes. As such, the first example could be represented in a cleaner way:

```kotlin
routing {
    listOrdersRoute()
    getOrderRoute()
    totalizeOrderRoute()
}

fun Route.listOrdersRoute() {
    get("/order") {

    }
}

fun Route.getOrderRoute() {
    get("/order/{id}") {
        
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id}/total") {
        
    }
}
```

For the full example demonstrating this approach,
see [legacy-interactive-website](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/legacy-interactive-website).

> For our application to scale when it comes to maintainability, it is recommended to follow certain [structuring patterns](server-application-structure.md).


## Trace routes {id="trace_routes"}

With [logging](server-logging.md) configured, Ktor enables route tracing that helps you determine why some routes are not being executed.
For example, if you [run](server-run.md) the application and make a request to a specified endpoint,
the application's output might look as follows:

```Console
TRACE Application - Trace for [missing-page]
/, segment:0 -> SUCCESS @ /
  /, segment:0 -> SUCCESS @ /
    /(method:GET), segment:0 -> FAILURE "Not all segments matched" @ /(method:GET)
Matched routes:
  No results
Route resolve result:
  FAILURE "No matched subtrees found" @ /
```

> To enable route tracing on the [Native server](server-native.md), 
> pass the _TRACE_  value to the `KTOR_LOG_LEVEL` environment variable when [running](server-run.md) the application.

[//]: # (title: Routing)
[//]: # (caption: Structured Handling of HTTP Requests)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/routing.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.routing.Routing)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/routing.html: - /features/routing.html)
[//]: # (ktor_version_review: 1.0.0)

Routing is a feature that is installed into an Application to simplify and structure page request handling.

This page explains the routing feature. Extracting information about a request,
and generating valid responses inside a route, is described on the [requests] and [responses] pages.

[requests]: /servers/calls/requests.html
[responses]: /servers/calls/responses.html

```kotlin
    application.install(Routing) {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/bye") {
            call.respondText("Good bye, World!")
        }
    }
```

`get`, `post`, `put`, `delete`, `head` and `options` functions are convenience shortcuts to a flexible and 
powerful routing system. 
In particular, `get` is an alias to `route(HttpMethod.Get, path) { handle(body) }`, where `body` is a lambda passed to the
`get` function. 



## Routing Tree

Routing is organized in a tree with a recursive matching system that is capable of handling quite complex rules
for request processing. The Tree is built with nodes and selectors. The Node contains handlers and interceptors, 
and the selector is attached to an arc which connects another node. If selector matches current routing evaluation context, 
the algorithm goes down to the node associated with that selector.
 
Routing is built using a DSL in a nested manner:
  
```kotlin
route("a") { // matches first segment with the value "a"
  route("b") { // matches second segment with the value "b"
     get {…} // matches GET verb, and installs a handler 
     post {…} // matches POST verb, and installs a handler
  }
}
```
  
```kotlin
method(HttpMethod.Get) { // matches GET verb
   route("a") { // matches first segment with the value "a"
      route("b") { // matches second segment with the value "b"
         handle { … } // installs handler
      }
   }
}
```  

Route resolution algorithms go through nodes recursively discarding subtrees where selector didn't match.

Builder functions:

* `route(path)` – adds path segments matcher(s), see below about [paths](#path)
* `method(verb)` – adds HTTP method matcher.
* `param(name, value)` – adds matcher for a specific value of the query parameter
* `param(name)` – adds matcher that checks for the existence of a query parameter and captures its value
* `optionalParam(name)` – adds matcher that captures the value of a query parameter if it exists
* `header(name, value)` – adds matcher that for a specific value of HTTP header, see below about [quality](#quality)

## Path

Building routing tree by hand would be very inconvenient. Thus there is `route` function that covers most of the use cases in a 
 simple way, using _path_.

`route` function (and respective HTTP verb aliases) receives a `path` as a parameter which is processed to build routing
tree. First, it is split into path segments by the `'/'` delimiter. Each segment generates a nested routing node.

These two variants are equivalent:

```kotlin
route("/foo/bar") { … } // (1)

route("/foo") {
   route("bar") { … } // (2)
}
```

#### Parameters
Path can also contain _parameters_ that match specific path segment and capture its value into `parameters` properties
of an application call:

```kotlin
get("/user/{login}") {
   val login = call.parameters["login"]
}
```

When user agent requests `/user/john` using GET method, this route is matched and `parameters` property
will have `"login"` key with value `"john"`.

#### Optional, Wildcard, Tailcard

Parameters and path segments can be optional or capture entire remainder of URI.

* `{param?}` – optional path segment, if it exists it's captured in the parameter
* `*` – wildcard, any segment will match, but shouldn't be missing
* `{...}`– tailcard, matches all the rest of the URI, should be last. Can be empty.
* `{param...}` – captured tailcard, matches all the rest of the URI and puts multiple values for each path segment
   into `parameters` using `param` as key. Use `call.parameters.getAll("param")` to get all values.
 
Examples:

```kotlin
get("/user/{login}/{fullname?}") { … } 
get("/resources/{path...}") { … } 
```

## Quality

It is not unlikely that several routes can match to the same HTTP request.

One example is matching on the `Accept` HTTP header which can have multiple values with specified priority (quality).

```kotlin
accept(ContentType.Text.Plain) { … }
accept(ContentType.Text.Html) { … }
```

The routing matching algorithm not only checks if a particular HTTP request matches a specific path in a routing tree, but
it also calculates the quality of the match and selects the routing node with the best quality.  Given the routes above,
which match on the Accept header, and given the request header `Accept: text/plain; q=0.5, text/html` will match
`text/html` because the quality factor in the HTTP header indicates a lower quality for`text/plain` (default is 1.0).

The Header `Accept: text/plain, text/*` will match `text/plain`. Wildcard matches are considered less specific than direct matches. Therefore the routing matching algorithm will consider them to have a lower quality.

Another example is making short URLs to named entities, e.g. users, and still being able to prefer specific pages like 
"settings".  An example would be 

* `https://twitter.com/kotlin` – displays user "kotlin"
* `https://twitter.com/settings` - displays settings page

This can be implemented like this:

```kotlin
get("/{user}") { … }
get("/settings") { … }
```
The parameter is considered to have a lower quality than a constant string, so that even if `/settings` matches both,
the second route will be selected.  

## Interception

When routing node is selected, the routing system builds a special pipeline to execute the node.
This pipeline consists of handler(s) for the selected node and any interceptors installed into nodes that
constitutes path from root to the selected node in order from top to bottom.

```kotlin
route("/portal") {
   route("articles") { … }
   route("admin") {
      intercept(ApplicationCallPipeline.Features) { … } // verify admin privileges
      route("article/{id}") { … } // manage article with {id}
      route("profile/{id}") { … } // manage profile with {id}
   }
}
```

Given the routing tree above, when request URI starts with `/portal/articles`, routing will handle 
call normally, but if the request is in `/portal/admin` section, it will first execute interceptor to validate
if the current user has enough privilege to access admin pages. 

Other examples could be installing JSON serialisation into `/api` section, 
loading user from the database in `/user/{id}` section and placing it into call's attributes, etc. 

## Extensibility
  
The `ktor-server-core` module contains a number of basic selectors to match method, path, headers and query parameters, but
you can easily add your own selectors to fit in even more complex logic. Implement `RouteSelector` and create
a builder function similar to built-in. 

Path parsing is not extensible.

## Tracing the routing decisions
{id="tracing"}

If you have problems trying to figure out why your route is not being executed,
Ktor provides a `trace` method inside the routing feature.

```kotlin
routing {
    trace { application.log.trace(it.buildText()) }
}
```

This method is executed whenever a call is done giving you a trace of the decisions
taken. As an example, for this routing configuration:

```kotlin
routing {
    trace { application.log.trace(it.buildText()) }
    get("/bar") { call.respond("/bar") }
    get("/baz") { call.respond("/baz") }
    get("/baz/x") { call.respond("/baz/x") }
    get("/baz/x/{optional?}") { call.respond("/baz/x/{optional?}") }
    get("/baz/{y}") { call.respond("/baz/{y}") }
    get("/baz/{y}/value") { call.respond("/baz/{y}/value") }
    get("/{param}") { call.respond("/{param}") }
    get("/{param}/x") { call.respond("/{param}/x") }
    get("/{param}/x/z") { call.respond("/{param}/x/z") }
    get("/*/extra") { call.respond("/*/extra") }

}
```

The output if requesting `/bar` would be:

```
Trace for [bar]
/, segment:0 -> SUCCESS @ /bar/(method:GET))
  /bar, segment:1 -> SUCCESS @ /bar/(method:GET))
    /bar/(method:GET), segment:1 -> SUCCESS @ /bar/(method:GET))
  /baz, segment:0 -> FAILURE "Selector didn't match" @ /baz)
  /{param}, segment:0 -> FAILURE "Better match was already found" @ /{param})
  /*, segment:0 -> FAILURE "Better match was already found" @ /*)
```

>Remember to remove or disable this function when going into production.
>
{type="note"}
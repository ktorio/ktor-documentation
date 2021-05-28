[//]: # (title: Resolution Algorithms)

<include src="lib.xml" include-id="outdated_warning"/>

[Routing](Routing_in_Ktor.md) is organized in a tree with a recursive matching system that is capable of handling quite complex rules
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

* `route(path)` – adds path segments matcher(s)
* `method(verb)` – adds HTTP method matcher.
* `param(name, value)` – adds matcher for a specific value of the query parameter
* `param(name)` – adds matcher that checks for the existence of a query parameter and captures its value
* `optionalParam(name)` – adds matcher that captures the value of a query parameter if it exists
* `header(name, value)` – adds matcher that for a specific value of HTTP header



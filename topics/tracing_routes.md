[//]: # (title: Tracing Routes)

<include src="lib.xml" element-id="outdated_warning"/>

If you have problems trying to figure out why your route is not being executed,
Ktor provides a `trace` method inside the routing plugin.

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

```text
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



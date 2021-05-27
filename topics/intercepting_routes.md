[//]: # (title: Intercepting Routes)

<include src="lib.xml" include-id="outdated_warning"/>

If you just want to intercept some calls for a specific route, you have to create a `Route` node (usually by calling `createChild`) and intercept that node.

For example, for creating a timeout for a route, you could do the following:

```kotlin
fun Route.routeTimeout(time: Long, unit: TimeUnit = TimeUnit.SECONDS, callback: Route.() -> Unit): Route {
    // With createChild, we create a child node for this received Route  
    val routeWithTimeout = this.createChild(object : RouteSelector(1.0) {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    // Intercepts calls from this route at the features step
    routeWithTimeout.intercept(ApplicationCallPipeline.Features) {
        withTimeout(time, unit) {
            proceed() // With proceed we can define code to be executed before and after the call
        }
    }
    
    // Configure this route with the block provided by the user
    callback(routeWithTimeout)

    return routeWithTimeout
}
```

## Intercepting any `Route` node

The `Route` class defines an intercept method that applies to that route node or any other inner route:

```kotlin
/// Installs an interceptor into this route which will be called when this or a child route is selected for a call
fun Route.intercept(phase: PipelinePhase, block: PipelineInterceptor<Unit, ApplicationCall>)
```

## Getting the route being handled
{id="route-from-call"}

You can get the route being handled by casting the `call: ApplicationCall` to `RoutingApplicationCall` that has a `route: Route` property.

## Getting the route path
{id="route-path"}

`Route` overrides the `toString()` method to generate a path to the route, something like:

```kotlin
override fun Route.toString() = when {
    parent == null -> "/"
    parent.parent == null -> "/$selector"
    else -> "$parent/$selector"
}
```

## How to intercept preventing additional executions

```kotlin
    intercept(ApplicationCallPipeline.Setup) {
        if (call.request.path() == "/admin/book") {
            call.respondText {
                "intercept book"
            }
            // Truncate the route response. If there is no finish() function,
            // the route /book will still respond to the processing, and the pipeline will be unwritable.
            return@intercept finish()
        } 
    }
```

## Hooking before and after routing

You can globally intercept the routing calls by using the events `Routing.RoutingCallStarted` and `Routing.RoutingCallFinished`:

```kotlin
pipeline.environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
    println("Route started: ${call.route}")
}

pipeline.environment.monitor.subscribe(Routing.RoutingCallFinished) { call: RoutingApplicationCall ->
    println("Route completed: ${call.route}")
}
```

You can see a full example of this in the [Metrics feature](https://github.com/ktorio/ktor/blob/main/ktor-features/ktor-metrics/jvm/src/io/ktor/metrics/dropwizard/DropwizardMetrics.kt).



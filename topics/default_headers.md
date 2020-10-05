[//]: # (title: Default Headers)

The `DefaultHeaders` [feature](Features.md) allows you to configure default headers that will be merged into each response. Installing this feature adds the standard `Server` and `Date` headers automatically.

## Install Default Headers {id="install"}
To install the `DefaultHeaders` feature, pass it to the `install` function. For example, you can do this inside the `main` function ...
```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(DefaultHeaders)
  // ...
}
```
... or enable default headers for a specified [module](Modules.md):
```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(DefaultHeaders)
    // ...
}
```
This adds the `Server` and `Date` headers into each response. If necessary, you can override the `Server`, as described in the [next chapter](#customize).
> Note that the `Date` header is cached due to performance reasons and cannot be overridden by using `DefaultHeaders`. If you need to override it, do not install the `DefaultHeaders` feature and use [route interception](#route_headers) instead.

## Customize Default Headers {id="customize"}
To customize a list of default headers, pass the desired header to `install`  by using the `header(name, value)` function. Below are a few examples:

* Pass a header name using a `HttpHeaders` value:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.ETag, "7c876b7e")
    }
```

* To override the `Server` header, use a corresponding `HttpHeaders` value:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "Custom")
    }
```

* To add a custom header, pass its name as a string value:
```kotlin
    install(DefaultHeaders) {
        header("Custom-Header", "Some value")
    }
```

## Customize Headers for Specific Routes {id="route_headers"}

If you need to add default headers for specific [routes](Routing_in_Ktor.md) only, you can use [route interception](intercepting_routes.md). In this case, you can check a request URI and add desired headers into a response, for example:
```kotlin
fun Route.listOrdersRoute() {
    intercept(ApplicationCallPipeline.Call) {
        if (call.request.uri == "/order") {
            call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
            // ...
        }
    }
    // ...
}
```

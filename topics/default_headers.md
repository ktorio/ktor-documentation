[//]: # (title: Default headers)

The `DefaultHeaders` [plugin](Plugins.md) adds the standard `Server` and `Date` headers into each response. Moreover, you can provide additional default headers and override the `Server` header.

## Install DefaultHeaders {id="install_feature"}

<var name="feature_name" value="DefaultHeaders"/>
<include src="lib.xml" include-id="install_feature"/>

The `DefaultHeaders` plugin adds the `Server` and `Date` headers into each response. If necessary, you can override the `Server`, as described in [](#override).


## Add additional headers {id="add"}
To customize a list of default headers, pass a desired header to `install`  by using the `header(name, value)` function. The `name` parameter accepts an `HttpHeaders` value, for example:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.ETag, "7c876b7e")
    }
```
To add a custom header, pass its name as a string value:
```kotlin
    install(DefaultHeaders) {
        header("Custom-Header", "Some value")
    }
```


## Override headers {id="override"}
To override the `Server` header, use a corresponding `HttpHeaders` value:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "Custom")
    }
```
Note that the `Date` header is cached due to performance reasons and cannot be overridden by using `DefaultHeaders`. If you need to override it, do not install the `DefaultHeaders` plugin and use [route interception](intercepting_routes.md) instead.




## Customize headers for specific routes {id="route_headers"}

If you need to add headers for a specific route only, you can append desired headers into a response. The code snippet below shows how to do this for the `/order` request:
```kotlin
get("/order") {
    call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
}
```
You can learn more about routing in %product% from [](Routing_in_Ktor.md).
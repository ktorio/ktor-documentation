[//]: # (title: Cookie/Header)

[Sessions](sessions.md) allow you to choose between two ways of transferring data within HTTP requests: cookies or custom headers. Cookies suit better for plain HTML applications while custom headers are intended for APIs (for both Fetch API and requesting headers from the server).


## Configure Cookie/Header {id="configure"}
[Sessions.Configuration](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-sessions/-configuration/index.html) provides the `cookie` and `header` methods for selecting how to transfer session data. For both ways, you can choose whether to pass the entire session data between the [client and server](client_server.md) or only the session ID and store data on the server. If you pass data to the client, you need to apply [transforms](transformers.md) to encrypt or authenticate sessions.


### Cookie {id="cookie"}
To pass session data using cookies, call the `cookie` method with the specified name and data class inside the `install(Sessions)` block:
```kotlin
install(Sessions) {
    cookie<SampleSession>("SAMPLE_SESSION")
}
```
In the example above, session data will be passed to the client using the `SAMPLE_SESSION` attribute added to the `Set-Cookie` header. You can configure other cookie attributes by passing them inside the `cookie` block. For example, the code snippet below shows how to specify a cookie's path and expiration time:
```kotlin
install(Sessions) {
    cookie<SampleSession>("SAMPLE_SESSION") {
        cookie.path = "/orders"
        cookie.maxAgeInSeconds = 1000
    }
}
```
If the required attribute is not exposed explicitly, use the `extensions` property. For example, you can pass the `SameSite`attribute in the following way:
```kotlin
install(Sessions) {
    cookie<SampleSession>("SAMPLE_SESSION") {
        cookie.extensions["SameSite"] = "lax"
    }
}
```
To learn more about available configurations settings, see [CookieConfiguration](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-cookie-configuration/index.html).


### Header {id="header"}
To pass session data using a custom header, call the `header` method with the specified name and data class inside the `install(Sessions)` block:
```kotlin
install(Sessions) {
    header<SampleSession>("SAMPLE_SESSION")
}
```
In the example above, session data will be passed to the client using the `SAMPLE_SESSION` header. 
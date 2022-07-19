[//]: # (title: Cookie/Header)

[Sessions](sessions.md) allow you to choose between two ways of transferring data within HTTP requests: cookies or custom headers. Cookies suit better for plain HTML applications while custom headers are intended for APIs (for both Fetch API and requesting headers from the server).


## Configure Cookie/Header {id="configure"}
[Sessions.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-sessions/-configuration/index.html) provides the `cookie` and `header` methods for selecting how to transfer session data. For both ways, you can choose whether to pass the entire session data between the [client and server](client_server.md) or only the session ID and store data on the server. If you pass data to the client, you need to apply [transforms](transformers.md) to encrypt or authenticate sessions.


### Cookie {id="cookie"}
To pass session data using cookies, call the `cookie` method with the specified name and data class inside the `install(Sessions)` block:
```kotlin
install(Sessions) {
    cookie<UserSession>("user_session")
}
```
In the example above, session data will be passed to the client using the `user_session` attribute added to the `Set-Cookie` header. You can configure other cookie attributes by passing them inside the `cookie` block. For example, the code snippet below shows how to specify a cookie's path and expiration time:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" include-lines="11-16"}

If the required attribute is not exposed explicitly, use the `extensions` property. For example, you can pass the `SameSite`attribute in the following way:
```kotlin
install(Sessions) {
    cookie<UserSession>("user_session") {
        cookie.extensions["SameSite"] = "lax"
    }
}
```
To learn more about available configurations settings, see [CookieConfiguration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-cookie-configuration/index.html).

> Before [deploying](deploy.md) your application to production, make sure the `secure` property is set to `true`. This enables transferring cookies via a [secure connection](ssl.md) only and protects session data from HTTPS downgrade attacks.
>
{type="warning"}


### Header {id="header"}
To pass session data using a custom header, call the `header` method with the specified name and data class inside the `install(Sessions)` block:
```kotlin
install(Sessions) {
    header<UserSession>("user_session")
}
```
In the example above, session data will be passed to the client using the `user_session` header. 
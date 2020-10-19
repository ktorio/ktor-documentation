[//]: # (title: Sessions)

Sessions provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, you can implement sessions by using cookies or custom headers, choose whether to store session data on the server or pass it to the client, sign and encrypt session data, and more.

In this topic, we'll look at how to configure sessions, install the `Sessions` feature, and set the session's content.


## Session Configuration Overview {id="configure"}
You can configure sessions in the following ways:
- *[How to pass data between the server and client](cookie_header.md)*: using cookies or custom headers. Cookies suit better for plain HTML applications while custom headers are intended for APIs.
- *[Where to store the session payload](client_server.md)*: on the client or server. You can pass the [serialized](serializers.md) session's data to the client using a cookie/header value or store the payload on the server and pass only a session ID.
- *[How to serialize session data](serializers.md)*: using a default format, JSON, or a custom engine.
- *[Where to store the payload on the server](storages.md)*: in memory, in a folder, or Redis. You can also implement a custom storage for keeping session data.
- *[How to transform the payload](transformers.md)*: you can sign or encrypt data sent to the client for security reasons.


## Install Sessions {id="install"}
Before installing a session, you need to create a [data class](https://kotlinlang.org/docs/reference/data-classes.html) for storing session data, for example:
```kotlin
data class LoginSession(val username: String, val count: Int)
```
You need to create several data classes if you are going to use several sessions. 

After creating the required data classes, you can install the `Sessions` feature by passing it to the `install` function in the application initialization code. Inside the `install` block, call the `cookie` or `header` function depending on how you want to [pass data between the server and client](cookie_header.md):
```kotlin
import io.ktor.features.*
import io.ktor.sessions.*
// ...
fun Application.module() {
    install(Sessions) {
        cookie<LoginSession>("LOGIN_SESSION")
    }
}
```
You can now [set the session content](#set-content), modify the session, or clear it.

### Multiple Sessions {id="multiple"}
If you need several sessions in your application, you need to create a separate data class for each session. For example, you can create separate data classes for storing a user login and settings:
```kotlin
data class LoginSession(val username: String, val count: Int)
data class SettingsSession(val username: String, val settings: Settings)
```
You can store a username on the server in a [directory storage](storages.md) and pass user preferences to the client.
```kotlin
install(Sessions) {
    cookie<LoginSession>("LOGIN_SESSION", directorySessionStorage(File(".sessions"), cached = true))
    cookie<SettingsSession>("SETTINGS_SESSION")
}
```
Note that session names should be unique.


## Set Session Content {id="set-content"}
To set the session content for a specific [route](Routing_in_Ktor.md), use the [call.sessions](https://api.ktor.io/%ktor_version%/io.ktor.sessions/sessions.html) property. The [set](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-current-session/set.html) method allows you to create a new session instance:
```kotlin
routing {
    get("/") {
        call.sessions.set(LoginSession(name = "John", value = 1))
    }
}
```
To get the session content, you can call [get](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-current-session/get.html) receiving one of the registered session types as type parameter:
```kotlin
routing {
    get("/") {
        val loginSession: LoginSession? = call.sessions.get<LoginSession>()
    }
}
```
To modify a session, for example, to increment a counter, you need to call the `copy` method of the data class:
```kotlin
val loginSession = call.sessions.get<LoginSession>() ?: LoginSession(username = "Initial", count = 0)
call.sessions.set(session.copy(value = loginSession.count + 1))
```
When you need to clear a session for any reason (for example, when a user logs out), call the `clear` function:
```kotlin
call.sessions.clear<LoginSession>()
```






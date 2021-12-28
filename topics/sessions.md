[//]: # (title: Sessions)

<var name="artifact_name" value="ktor-server-sessions"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<p>Code examples:
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/session-cookie-client">session-cookie-client</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/session-cookie-server">session-cookie-server</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/session-header-server">session-header-server</a>,
</p>
</microformat>

<excerpt>
Sessions provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client.
</excerpt>

Sessions provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, you can implement sessions by using cookies or custom headers, choose whether to store session data on the server or pass it to the client, sign and encrypt session data, and more.

In this topic, we'll look at how to configure sessions, install the `Sessions` plugin, and set the session's content.

## Add dependencies {id="add_dependencies"}
To enable support for sessions, you need to include the `%artifact_name%` artifact in the build script:

<include src="lib.xml" include-id="add_ktor_artifact"/>


## Session configuration overview {id="configure"}
You can configure sessions in the following ways:
- *[How to pass data between the server and client](cookie_header.md)*: using cookies or custom headers. Cookies suit better for plain HTML applications while custom headers are intended for APIs.
- *[Where to store the session payload](client_server.md)*: on the client or server. You can pass the [serialized](serializers.md) session's data to the client using a cookie/header value or store the payload on the server and pass only a session ID.
- *[How to serialize session data](serializers.md)*: using a default format, JSON, or a custom engine.
- *[Where to store the payload on the server](storages.md)*: in the server memory or in a folder. You can also implement a custom storage for keeping session data.
- *[How to transform the payload](transformers.md)*: you can sign or encrypt data sent to the client for security reasons.


## Install Sessions {id="install"}
Before installing a session, you need to create a [data class](https://kotlinlang.org/docs/data-classes.html) for storing session data, for example:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="8"}

Note that you can optionally inherit this class from [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) to use a session for [authentication](session-auth.md).

> You need to create several data classes if you are going to use [several sessions](#multiple). 

After creating the required data classes, you can install the `Sessions` plugin by passing it to the `install` function in the application initialization code. Inside the `install` block, call the `cookie` or `header` function depending on how you want to [pass data between the server and client](cookie_header.md):

```kotlin
import io.ktor.server.sessions.*
// ...
fun Application.module() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
}
```
You can now [set the session content](#set-content), modify the session, or clear it.

### Multiple sessions {id="multiple"}
If you need several sessions in your application, you need to create a separate data class for each session. For example, you can create separate data classes for storing a user data and settings:
```kotlin
data class UserSession(val id: String, val count: Int)
data class SettingsSession(val id: String, val settings: Settings)
```
You can store a user identifier on the server in a [directory storage](storages.md) and pass user preferences to the client.
```kotlin
install(Sessions) {
    cookie<UserSession>("user_session", directorySessionStorage(File(".sessions"), cached = true))
    cookie<SettingsSession>("settings_session")
}
```
Note that session names should be unique.


## Get and set session content {id="set-content"}
To set the session content for a specific [route](Routing_in_Ktor.md), use the `call.sessions` property. The `set` method allows you to create a new session instance:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="17-21,37"}

To get the session content, you can call `get` receiving one of the registered session types as type parameter:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="17,23-24,31,37"}

To modify a session, for example, to increment a counter, you need to call the `copy` method of the data class:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="26"}

When you need to clear a session for any reason (for example, when a user logs out), call the `clear` function:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="17,33-37"}






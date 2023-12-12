[//]: # (title: Sessions)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="Sessions"/>
<var name="package_name" value="io.ktor.server.sessions"/>
<var name="artifact_name" value="ktor-server-sessions"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-client">session-cookie-client</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-server">session-cookie-server</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-header-server">session-header-server</a>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
The Sessions plugin provides a mechanism to persist data between different HTTP requests.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-sessions.html) plugin provides a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, you can implement sessions by using cookies or custom headers, choose whether to store session data on the server or pass it to the client, sign and encrypt session data and more.

In this topic, we'll look at how to install the `%plugin_name%` plugin, configure it, and access session data inside [route handlers](Routing_in_Ktor.md#define_route).

## Add dependencies {id="add_dependencies"}
To enable support for sessions, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install Sessions {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>


## Session configuration overview {id="configuration_overview"}
To configure the `%plugin_name%` plugin, you need to perform the following steps:
1. *[Create a data class](#data_class)*: before configuring a session, you need to create a [data class](https://kotlinlang.org/docs/data-classes.html) for storing session data.
2. *[Choose how to pass data between the server and client](#cookie_header)*: using cookies or a custom header. Cookies suit better for plain HTML applications, while custom headers are intended for APIs.
3. *[Choose where to store the session payload](#client_server)*: on the client or server. You can pass the serialized session's data to the client using a cookie/header value or store the payload on the server and pass only a session identifier.

   If you want to store the session payload on the server, you can *[choose how to store it](#storages)*: in the server memory or in a folder. You can also implement a custom storage for keeping session data.
4. *[Protect session data](#protect_session)*: to protect sensitive session data passed to the client, you need to sign and encrypt the session's payload.


After configuring `%plugin_name%`, you can [get and set session data](#use_sessions) inside [route handlers](Routing_in_Ktor.md#define_route).


## Create a data class {id="data_class"}

Before configuring a session, you need to create a [data class](https://kotlinlang.org/docs/data-classes.html) for storing session data. 
For example, the `UserSession` class below will be used to store the session ID and the number of page views:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="9"}

You need to create several data classes if you are going to use several sessions.

> You can optionally inherit your data class from [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) to use a session for [](authentication.md). Learn more from [](session-auth.md).

## Pass session data: Cookie vs Header {id="cookie_header"}

### Cookie {id="cookie"}
To pass session data using cookies, call the `cookie` function with the specified name and data class inside the `install(Sessions)` block:
```kotlin
install(Sessions) {
    cookie<UserSession>("user_session")
}
```
In the example above, session data will be passed to the client using the `user_session` attribute added to the `Set-Cookie` header. You can configure other cookie attributes by passing them inside the `cookie` block. For example, the code snippet below shows how to specify a cookie's path and expiration time:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="12,15-17,19-20"}

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
To pass session data using a custom header, call the `header` function with the specified name and data class inside the `install(Sessions)` block:

```kotlin
install(Sessions) {
    header<CartSession>("cart_session")
}
```

In the example above, session data will be passed to the client using the `cart_session` custom header. 
On the client side, you need to append this header to each request to get session data.

> If you use the [CORS](cors.md) plugin to handle cross-origin requests, add your custom header to the `CORS` configuration as follows:
> ```kotlin
> install(CORS) {
>     allowHeader("cart_session")
>     exposeHeader("cart_session")
> }
> ```


## Store session payload: Client vs Server {id="client_server"}

In Ktor, you can manage the session data in two ways:
- _Pass session data between the client and server_.
   
  If you pass only the session name to the [cookie or header](#cookie_header) function, session data will be passed between the client and server. In this case, you need to [sign and encrypt](#protect_session) the session's payload to protect sensitive session data passed to the client.
- _Store session data on the server and pass only a session ID between the client and server_.
   
  In such a case, you can choose [where to store the payload](#storages) on the server. For example, you can store session data in memory, in a specified folder, or you can implement your own custom storage.



## Store session payload on server {id="storages"}

Ktor allows you to store session data [on the server](#client_server) and pass only a session ID between the server and the client. In this case, you can choose where to keep the payload on the server.

### In-memory storage {id="in_memory_storage"}
[SessionStorageMemory](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-session-storage-memory/index.html) enables storing a session's content in memory. This storage keeps data while the server is running and discards information once the server stops. For example, you can store cookies in the server memory as follows:

```kotlin
```
{src="snippets/session-cookie-server/src/main/kotlin/com/example/Application.kt" include-lines="14,17"}

You can find the full example here: [session-cookie-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-server).

> Note that `SessionStorageMemory` is intended for development only.

### Directory storage {id="directory_storage"}

[directorySessionStorage](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/directory-session-storage.html) can be used to store a session's data in a file under the specified directory. For example, to store session data in a file under the `build/.sessions` directory, create the `directorySessionStorage` in this way:
```kotlin
```
{src="snippets/session-header-server/src/main/kotlin/com/example/Application.kt" include-lines="15,17"}

You can find the full example here: [session-header-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-header-server).

### Custom storage {id="custom_storage"}

Ktor provides the [SessionStorage](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-session-storage/index.html) interface that allows you to implement a custom storage.
```kotlin
interface SessionStorage {
    suspend fun invalidate(id: String)
    suspend fun write(id: String, value: String)
    suspend fun read(id: String): String
}
```
All three functions are [suspending](https://kotlinlang.org/docs/composing-suspending-functions.html). You can use [SessionStorageMemory](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-plugins/ktor-server-sessions/jvmAndNix/src/io/ktor/server/sessions/SessionStorageMemory.kt) as a reference.


## Protect session data {id="protect_session"}

### Sign session data {id="sign_session"}

Signing session data prevents modifying a session's content but allows users to see this content.
To sign a session, pass a sign key to the `SessionTransportTransformerMessageAuthentication` constructor and pass this instance to the `transform` function:

```kotlin
```
{src="snippets/session-cookie-server/src/main/kotlin/com/example/Application.kt" include-lines="12-18"}

`SessionTransportTransformerMessageAuthentication` uses `HmacSHA265` as the default authentication algorithm, which can be changed. 

### Sign and encrypt session data {id="sign_encrypt_session"}

Signing and encrypting session data prevents reading and modifying a session's content.
To sign and encrypt a session, pass a sign/encrypt keys to the `SessionTransportTransformerEncrypt` constructor and pass this instance to the `transform` function:

```kotlin
```

{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="12-20"}

> Note that the [encryption method has been updated](migrating-3.md#session-encryption-method-update) in Ktor
> version `3.0.0`. If you are migrating from an earlier version, use the `backwardCompatibleRead` property in the
> constructor of `SessionTransportTransformerEncrypt` to ensure compatibility with existing sessions.
>
{style="note"}

By default, `SessionTransportTransformerEncrypt` uses the `AES` and `HmacSHA256` algorithms, which can be changed. 

> Note that signing/encryption keys shouldn't be specified in code. You can use a custom group in the [configuration file](Configuration-file.topic#configuration-file-overview) to store signing/encryption keys and initialize them using [environment variables](Configuration-file.topic#environment-variables).
>
{type="warning"}



## Get and set session content {id="use_sessions"}
To set the session content for a specific [route](Routing_in_Ktor.md), use the `call.sessions` property. The `set` method allows you to create a new session instance:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="22-25"}

To get the session content, you can call `get` receiving one of the registered session types as type parameter:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="27-28,35"}

To modify a session, for example, to increment a counter, you need to call the `copy` method of the data class:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="27-35"}

When you need to clear a session for any reason (for example, when a user logs out), call the `clear` function:

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="37-40"}

You can find the full example here: [session-cookie-client](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-client).


## Examples {id="examples"}

The runnable examples below demonstrate how to use the `%plugin_name%` plugin:

- [session-cookie-client](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-client) shows how to pass [signed and encrypted](#sign_encrypt_session) session payload to the [client](#client_server) using [cookies](#cookie).
- [session-cookie-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-server) shows how to keep session payload in a [server memory](#in_memory_storage) and pass a [signed](#sign_session) session ID to the client using [cookies](#cookie).
- [session-header-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-header-server) shows how to keep session payload on a server in a [directory storage](#directory_storage) and pass [signed](#sign_session) session ID to the client using a [custom header](#header).





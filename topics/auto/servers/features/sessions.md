[//]: # (title: Sessions)
[//]: # (caption: Handle Conversations with Sessions)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/sessions.html)
[//]: # (children: /servers/features/sessions/)
[//]: # (keywords: custom session serializers, custom session transformers, custom session storage providers)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (artifact2: io.ktor)
[//]: # (class: io.ktor.sessions.Sessions)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/sessions.html: - /features/sessions.html)
[//]: # (ktor_version_review: 1.0.0)



A session is a mechanism to persist data between different HTTP requests. Establishing a conversational
context into the otherwise stateless nature of HTTP. They allow servers to keep a piece of information associated
with the client during a sequence of HTTP requests and responses.
 
Different use-cases include: authentication and authorization, user tracking, keeping information
at client like a shopping cart, and more.

Sessions are typically implemented by employing `Cookies`, but could also be done using headers for example
to be consumed by other backends or an AJAX requests.

They are either client-side when the entire serialized object goes back and forth between the client and the server,
or server-side when only the session ID is transferred and the associated data is stored entirely in the server. 





{% include feature.html %}

## Installation
{id="installation "}

Sessions are usually represented as immutable data classes (and session is changed by calling the `.copy` method):

```kotlin
data class SampleSession(val name: String, val value: Int)
```

A simple Sessions installation looks like this:

```kotlin
install(Sessions) {
    cookie<SampleSession>("COOKIE_NAME")
}
```

And a more advanced installation could be like:

```kotlin
install(Sessions) {
    cookie<SampleSession>(
        "SESSION_FEATURE_SESSION_ID",
        directorySessionStorage(File(".sessions"), cached = true)
    ) {
        cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
    }
}
```

To configure sessions you have to to specify a [cookie/header](/servers/features/sessions/cookie-header.html) name,
optional [server-side storage](/servers/features/sessions/client-server.html), and a class associated to the session.

If you want to further customize sessions. Please read the [extending](#extending) section.

Since there are several combinations for configuring sessions, there is a section about [deciding how to configure sessions](#configuring).
{ .note}

## Usage
{id="usage "}

In order to access or set the session content, you have to use the `call.sessions` property:

To get the session content, you can call the `call.sessions.get` method receiving as type parameter one
of the registered session types:

```kotlin
routing {
    get("/") {
        // If the session was not set, or is invalid, the returned value is null.
        val mySession: MySession? = call.sessions.get<MySession>()
    }
}
```

To create or modify current session you just call the `set` method of the `sessions` property with the value of the
data class: 

```kotlin
call.sessions.set(MySession(name = "John", value = 12))
```

To modify a session (for example incrementing a counter), you have to call the `.copy` method of the `data class`:

```kotlin
val session = call.sessions.get<MySession>() ?: MySession(name = "Initial", value = 0)  
call.sessions.set(session.copy(value = session.value + 1))
```

When a user logs out, or a session should be cleared for any other reason, you can call the `clear` function:

```kotlin
call.sessions.clear<MySession>()
```

After calling this, retrieving that session will return null, until set again.

<div markdown='1'>
When handling requests, you can get, set, or clear your sessions:

```kotlin
val session = call.sessions.get<SampleSession>() // Gets a session of this type or null if not available
call.sessions.set(SampleSession(name = "John", value = 12)) // Sets a session of this type
call.sessions.clear<SampleSession>() // Clears the session of this type 
```
</div>
{ .note.summarizing }

## Multiple sessions
{id="multiple-sessions"}

Since there could be several conversational states for a single application, you can install multiple session mappings.
For example:

* Storing user preferences, or cart information as a client-side cookie.
* While storing the user login inside a file on the server.

```kotlin
application.install(Sessions) {
    cookie<Session1>("Session1") // install a cookie stateless session
    header<Session2>("Session2", sessionStorage) { // install a header server-side session
        transform(SessionTransportTransformerDigest()) // sign the ID that travels to client
    }
}
```

```kotlin
install(Sessions) {
    cookie<SessionCart>("SESSION_CART_LIST") {
        cookie.path = "/shop" // Just accessible in '/shop/*' subroutes
    }
    cookie<SessionLogin>(
        "SESSION_LOGIN",
        directorySessionStorage(File(".sessions"), cached = true)
    ) {
        cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
        transform(SessionTransportTransformerDigest()) // sign the ID that travels to client
    }
}
```

For multiple session mappings, _both_ type and name should be unique.
{ .note} 

## Configuration
{id="configuration"}

You can configure the sessions in several different ways:

* *Where is the payload stored:* client-side, or server-side.
* *How is the payload or the session id transferred:* Using cookies or headers.
* *How are they serialized:* Using an internal format, JSON, a custom engine...
* *Where is the payload stored in the server:* In memory, a folder, redis...
* *Payload transformations:* Encrypted, authenticated...

Since sessions can be implemented by various techniques, there is an extensive configuration facility to set them up:

* `cookie` will install cookie-based transport
* `header` will install header-based transport 

Each of these functions will get the name of the cookie or header. 

If a function is passed an argument of type `SessionStorage` it will use the storage to save the session, otherwise
it will serialize the data into the cookie/header value.

Each of these functions can receive an optional configuration lambda.

For cookies, the receiver is `CookieSessionBuilder` which allows you to:

* specify custom `serializer`
* add a value `transformer`, like signing or encrypting
* specify the cookie configuration such as duration, encoding, domain, path and so on

For headers, the receiver is `HeaderSessionBuilder` which allows `serializer` and `transformer` customization.

For cookies & headers that are server-side with a `SessionStorage`, additional configuration is `identity` function
that should generate a new ID when the new session is created.

## Deciding how to configure sessions
{id="configuring"}

### Cookie vs Header

* Use [**Cookies**](/servers/features/sessions/cookie-header.html#cookies) for plain HTML applications.
* Use [**Header**](/servers/features/sessions/cookie-header.html#headers) for APIs or for XHR requests if it is simpler for your http clients.

### Client vs Server

* Use [**Server Cookies**](/servers/features/sessions/client-server.html#server-cookies) if you want to prevent session replays or want to further increase security
  * Use `SessionStorageMemory` for development if you want to drop sessions after stopping the server
  * Use `directorySessionStorage` for production environments or to keep sessions after restarting the server
* Use [**Client Cookies**](/servers/features/sessions/client-server.html#client-cookies) if you want a simpler approach without the storage on the backend
  * Use it plain if you want to modify it on the fly at the client for testing purposes and don't care about the modifications
  * Use it with transform authenticating and optionally encrypting it to prevent modifications
  * **Do not** use it at all if your session payload is vulnerable to replay attacks. [Security examples here](/servers/features/sessions/client-server.html#security).

## Baked snippets

### Storing the contents of the session in a cookie

Since no SessionStorage is provided as a `cookie` second argument its contents will be stored in the cookie.

```kotlin
install(Sessions) {
    val secretHashKey = hex("6819b57a326945c1968f45236589")
    
    cookie<SampleSession>("SESSION_FEATURE_SESSION") {
        cookie.path = "/"
        transform(SessionTransportTransformerMessageAuthentication(secretHashKey, "HmacSHA256"))
    }
}
```

### Storing a session id in a cookie, and storing session contents in-memory
{id="SessionStorageMemory "}

`SessionStorageMemory` don't have parameters at this point.

```kotlin
install(Sessions) {
    cookie<SampleSession>("SESSION_FEATURE_SESSION_ID", SessionStorageMemory()) {
        cookie.path = "/"
    }
}
```

Alongside SessionStorage there is a `SessionStorageMemory` class that you can use for development.
It is a simple implementation that will keep sessions in-memory, thus all the sessions are dropped
once you shutdown the server and will constantly grow in memory since this implementation does not discard
the old sessions at all.

This implementation is not intended for production environments.

### Storing a session id in a cookie, and storing session contents in a file
{id="directorySessionStorage "}

You have to include an additional artifact for the `directorySessionStorage` function.

`compile("io.ktor:ktor-server-sessions:$ktor_version") // Required for directorySessionStorage`

```kotlin
install(Sessions) {
    cookie<SampleSession>(
        "SESSION_FEATURE_SESSION_ID",
        directorySessionStorage(File(".sessions"), cached = true)
    ) {
        cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
    }
}
```

As part of the `io.ktor:ktor-server-sessions` artifact, there is a `directorySessionStorage` function
which utalizes a session storage that will use a folder for storing sessions on disk.

This function has a first argument of type `File` that is the folder that will store sessions (it will be created
if it doesn't exist already).

There is also an optional cache argument, which when set, will keep a 60-second in-memory cache to prevent
calling the OS and reading the session from disk each time.


### Storing a session id in a cookie, and storing session contents in redis
{id="redisStorage "}

> <https://github.com/ktorio/ktor/pull/504>{ target="_blank"}

```kotlin
val redis = RedisClient()
install(Sessions) {
    val cookieName = "SESSION"
    val sessionStorage = RedisSessionStorage(redis, ttlSeconds = 7 * 24 * 3_600) // Sessions lasts up to 7 days
    cookie<TestSession>(cookieName, sessionStorage)
}
```

{% include artifact.html kind="class" class="io.ktor.sessions.RedisSessionStorage" artifact="io.ktor:ktor-server-session-redis:$ktor_version" %}



## Extending
{id="extending "}

Sessions are designed to be extensible, and there are some cases where you might want to further compose
or change the default sessions behaviour.

For example by using custom encryption or authenticating algorithms for the transport value, or to store
your session information server-side to a specific database.

You can define [custom transformers], [custom serializers] and [custom storages].

[custom transformers]: /servers/features/sessions/transformers.html
[custom serializers]: /servers/features/sessions/serializers.html
[custom storages]: /servers/features/sessions/storages.html
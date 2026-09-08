[//]: # (title: Type-safe session authentication)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>, <code>io.ktor:ktor-server-sessions</code>
</p>
</tldr>

<link-summary>
The session scheme of the type-safe authentication API gives routes a non-null principal and read-write access to the
stored session.
</link-summary>

The `session` scheme is part of the [type-safe authentication API](server-typed-auth.md). It separates two things that
the classic [session authentication](server-session-auth.md) keeps together:

* The **session** is the value you store for the caller, such as an access token or a user ID.
* The **principal** is what your route handlers work with, such as a full user record.

Inside a protected route, `call.session` gives you the stored session and `call.principal` gives you the principal. Both
are non-null and correctly typed.

<include from="lib.topic" element-id="typed_auth_experimental"/>

## Create a session scheme {id="create-scheme"}

The `session<S, P>()` factory takes the session type first and the principal type second. The `validate` block turns a
session into a principal or returns `null` to reject the session.

By default, a request without a valid session gets `401 Unauthorized`. For a browser application, a redirect to the
sign-in page is usually better. Set `onUnauthorized` to change the response:

```kotlin
data class UserSession(val userId: String)
data class User(val id: String, val email: String)

val sessionAuth = session<UserSession, User>("auth-session") {
    validate { session ->
        userRepository.findById(session.userId)
    }
    onUnauthorized = {
        call.respondRedirect("/login")
    }
}
```

A handler passed to `authenticateWith()` overrides this one for that route. See
[](server-typed-auth.md#failures) for the full order.

## Choose a transport {id="transport"}

The `transport` property controls how the session travels between the client and the server:

| Transport                       | What the client holds              | Where session data lives             |
|---------------------------------|------------------------------------|--------------------------------------|
| `SessionTransportType.CookieId` | A session ID in a cookie           | On the server, in a `SessionStorage` |
| `SessionTransportType.HeaderId` | A session ID in a header           | On the server, in a `SessionStorage` |
| `SessionTransportType.Cookie`   | The serialized session in a cookie | On the client                        |
| `SessionTransportType.Header`   | The serialized session in a header | On the client                        |

The default is `CookieId` backed by an in-memory storage, which keeps session data on the server:

```kotlin
val storage = SessionStorageMemory()

val sessionAuth = session<UserSession, User>("auth-session") {
    transport = SessionTransportType.CookieId(storage) {
        cookie.path = "/"
        cookie.maxAgeInSeconds = 3600
        cookie.httpOnly = true
    }
    validate { session ->
        userRepository.findById(session.userId)
    }
}
```

`SessionStorageMemory` loses everything on restart and is not shared between instances, so it suits local development.
In production, use a persistent storage such as `directorySessionStorage()`, or your own
`SessionStorage` implementation.

> The `Cookie` and `Header` transports send the session value to the client. That value is the caller's identity, so
> without protection a client can forge it and sign in as anyone. If you use a by-value transport, add a
> transformer such as `SessionTransportTransformerEncrypt`:
> ```kotlin
> val transformer = SessionTransportTransformerEncrypt(
>     encryptKey, 
>     signKey
> )
> transport = SessionTransportType.Cookie {
>     transform(transformer)
> }
> ```
>
{style="warning"}

## Install the Sessions plugin {id="install-sessions"}

A session scheme reads the session through the [Sessions](server-sessions.md) plugin, so the plugin must be installed
before any route uses the scheme. Pass the scheme to `install()` and Ktor applies the transport you configured:

```kotlin
fun Application.module() {
    install(sessionAuth)

    routing {
        authenticateWith(sessionAuth) {
            get("/profile") {
                call.respondText(call.principal.email)
            }
        }
    }
}
```

You can also install it on a single route subtree:

```kotlin
routing {
    route("/app") {
        install(sessionAuth)

        authenticateWith(sessionAuth) {
            get("/profile") { /* ... */ }
        }
    }
}
```

If you configure `Sessions` yourself, call `applyTransport()` inside the configuration block so the scheme and the
plugin agree on the name, type, and transport:

```kotlin
install(Sessions) {
    sessionAuth.applyTransport()
    cookie<OtherSession>("other-session")
}
```

If the plugin is missing, or if it has no provider matching the scheme's name and session type, the scheme fails at
startup.

## Read and change the session {id="read-write"}

Inside a route protected by a session scheme, `call.session` is a read-write property:

```kotlin
routing {
    authenticateWith(sessionAuth) {
        get("/profile") {
            val session = call.session
            call.respondText("Signed in as ${session.userId}")
        }

        post("/switch-team") {
            val teamId = call.receiveText()
            call.session = call.session.copy(teamId = teamId)
            call.respondText("Switched")
        }
    }
}
```

These accessors are available in the same block:

| Accessor                 | Purpose                                                                    |
|--------------------------|----------------------------------------------------------------------------|
| `call.session`           | Reads or replaces the session.                                             |
| `call.updateSession { }` | Reads the session, applies your change, stores the result, and returns it. |
| `call.clearSession()`    | Removes the session, which signs the caller out.                           |

Use `updateSession` when the new value depends on the old one:

```kotlin
post("/increment") {
    val updated = call.updateSession {
        it.copy(visits = it.visits + 1)
    }
    call.respondText("Visits: ${updated.visits}")
}
```

Inside `authenticateWithOptional()`, there may be no session at all, so `call.session` is not available. Use
`call.sessionOrNull` instead:

```kotlin
routing {
    authenticateWithOptional(sessionAuth) {
        get("/greeting") {
            val message = when (call.sessionOrNull) {
                null -> "Hello, guest"
                else -> "Welcome back"
            }
            call.respondText(message)
        }
    }
}
```

## Sign users in and out {id="login-logout"}

`setSession()` and `clearSession()` on the scheme work on any route, whether or not the scheme protects it.

Sign-in belongs on a route the scheme does not protect. The caller has no session yet, so a protected route would
reject the request before your handler runs:

```kotlin
routing {
    post("/login") {
        val credentials = call.receive<LoginRequest>()
        val user = userRepository.authenticate(credentials)
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized
            )

        sessionAuth.setSession(UserSession(userId = user.id))
        call.respondText("Signed in")
    }

    post("/logout") {
        sessionAuth.clearSession()
        call.respondText("Signed out")
    }
}
```

## Update a session during authentication {id="transform-session"}

Use `transformSession` to change the stored session as part of authentication, before `validate` runs. This is useful
for sessions that carry an expiring token.

Return the session to use for this request, or `null` to reject it. Ktor writes the session back only when the value you
return differs from the incoming one:

```kotlin
val sessionAuth = session<UserSession, User>("auth-session") {
    transformSession { session ->
        if (session.expiresAt > Clock.System.now()) {
            session
        } else {
            tokenService.refresh(session.refreshToken)
        }
    }
    validate { session ->
        userRepository.findById(session.userId)
    }
}
```

## Add CSRF protection {id="csrf"}

Cookie transports send the session automatically on every request, including requests started by another site. Use
`csrfProtection` to install the
[CSRF](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-csrf/io.ktor.server.plugins.csrf/-c-s-r-f.html)
plugin for the routes this scheme protects:

```kotlin
val sessionAuth = session<UserSession, User>("auth-session") {
    csrfProtection {
        allowOrigin("https://app.example.com")
        originMatchesHost()
    }
    validate { session ->
        userRepository.findById(session.userId)
    }
}
```

## What's next {id="next"}

* [](server-typed-auth.md) covers roles, optional authentication, and the rest of the API.
* [](server-oauth2-flows.md) uses a session scheme to keep users signed in after an OAuth sign-in.
* [](server-sessions.md) covers the Sessions plugin, storages, and transformers in detail.

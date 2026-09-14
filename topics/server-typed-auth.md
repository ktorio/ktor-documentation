[//]: # (title: Type-safe authentication)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
</tldr>

<link-summary>
The type-safe authentication scheme API binds the principal type to the route, so you can read a non-null principal without
casts or null checks.
</link-summary>

Ktor provides a type-safe authentication scheme API that binds an authentication scheme to a principal type. You create a
scheme once, pass that scheme to a route, and read the principal without a cast or a null check.

> This API is an alternative to the [`install(Authentication)`](server-auth.md) approach. Both APIs can be used in the same
> application, and you can nest routes that use one API inside routes that use the other.
> 
{style="tip"}

## Add dependencies {id="add_dependencies"}

To use typed authentication, add the `ktor-server-auth` artifact to your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

If using the `jwt` scheme, add the `ktor-server-auth-jwt` artifact:

<var name="artifact_name" value="ktor-server-auth-jwt"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

If using the `apiKey` scheme, add the `ktor-server-auth-api-key` artifact:

<var name="artifact_name" value="ktor-server-auth-api-key"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Enable the API {id="prerequisites"}

The API is marked with `@ExperimentalKtorApi`, so you must opt in:

```kotlin
@OptIn(ExperimentalKtorApi::class)
fun Application.module() {
    // ...
}
```

The route builders use Kotlin [context parameters](https://kotlinlang.org/docs/context-parameters.html). Kotlin 2.4.0
enables context parameters by default. With Kotlin 2.2.x or 2.3.x, enable them in your build script:

```kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
```

## Define a principal {id="principal"}

A principal represents the identity or other information that your application associates with an authenticated caller.
The principal type must be non-null.

```kotlin
data class User(
    val id: String,
    val email: String
)
```

## Create a scheme {id="create-scheme"}

Each authentication method provides a factory function that takes a principal type, a name, and a configuration block. The
`validate {}` block returns your principal type, or `null` when the credentials are rejected:

```kotlin
val jwtAuth = jwt<User>("my-jwt") {
    realm = "my-app"
    verifier(jwkProvider, issuer)
    validate { credential ->
        val payload = credential.payload
        User(
            id = payload.subject,
            email = payload.getClaim("email").asString()
        )
    }
}
```

The factory returns a value. Store it and pass it to the routes that require it.

| Factory                             | Artifact                   | Notes                                   |
|-------------------------------------|----------------------------|-----------------------------------------|
| `basic<P>()`                        | `ktor-server-auth`         | See [](server-basic-auth.md)            |
| `digest<P>()`                       | `ktor-server-auth`         | JVM only. See [](server-digest-auth.md) |
| `bearer<P>()`                       | `ktor-server-auth`         | See [](server-bearer-auth.md)           |
| `form<P>()`                         | `ktor-server-auth`         | See [](server-form-based-auth.md)       |
| `session<S, P>()`                   | `ktor-server-auth`         | See [](server-typed-session-auth.md)    |
| `apiKey<P>()`                       | `ktor-server-auth-api-key` | See [](server-api-key-auth.md)          |
| `jwt<P>()`                          | `ktor-server-auth-jwt`     | JVM only. See [](server-jwt.md)         |
| `oauth2()`, `oauth2Session<P, S>()` | `ktor-server-auth`         | See [](server-oauth2-flows.md)          |

Scheme names must be unique. If you create two different schemes with the same name, the second one fails when a route
tries to use it.

## Protect routes {id="protect-routes"}

To protect routes, pass a scheme to the `authenticateWith()` function. Inside the block, `call.principal` has the principal
type defined by the scheme and is guaranteed to be non-null:

```kotlin
routing {
    authenticateWith(jwtAuth) {
        get("/profile") {
            val user: User = call.principal
            call.respondText(user.email)
        }
    }
}
```

## Make authentication optional {id="optional"}

Use the `authenticateWithOptional()` function when a route should serve both signed-in and anonymous callers. Inside the
block, use `call.principalOrNull` to access the principal:

```kotlin
routing {
    authenticateWithOptional(jwtAuth) {
        get("/me") {
            val user = call.principalOrNull
            call.respondText(user?.email ?: "anonymous")
        }
    }
}
```

A request without credentials succeeds and leaves `call.principalOrNull` as `null`. A request with invalid credentials
still fails.

## Accept multiple schemes {id="any-of"}

Use the `authenticateWithAnyOf()` function to accept more than one scheme on the same route. Ktor tries the schemes in
the order you list them, and the first one that succeeds provides the principal.

All schemes must produce a principal that fits a common type, which you declare on the call:

```kotlin
interface AppUser {
    val email: String
}

data class JwtUser(
    override val email: String,
    val id: String
) : AppUser

data class ApiUser(override val email: String) : AppUser

val jwtScheme = jwt<JwtUser>("my-jwt") { /* ... */ }
val apiKeyScheme = apiKey<ApiUser>("my-api-key") { /* ... */ }

routing {
    // AppUser is 
    authenticateWithAnyOf<AppUser>(jwtScheme, apiKeyScheme) {
        get("/profile") {
            call.respondText(call.principal.email)
        }
    }
}
```

> Inside `authenticateWithAnyOf()`, only `call.principal` is available. Extras that belong to a single scheme, such
> as `call.session` on a [session scheme](server-typed-session-auth.md), are not.
>
{style="note"}

## Allow anonymous callers {id="anonymous"}

Use the `orAnonymous()` function to build a scheme that serves callers without credentials. A request without
credentials gets the principal your block returns. A request with invalid credentials still fails.

The result is a scheme whose principal type is a common supertype of the two:

```kotlin
interface Identity

data class AuthenticatedUser(val id: String) : Identity
data class GuestUser(val label: String = "guest") : Identity

val feedAuth = jwt<AuthenticatedUser>("my-jwt") {
    verifier(jwkProvider, issuer)
    validate { credential ->
        AuthenticatedUser(credential.payload.subject)
    }
}.orAnonymous { GuestUser() }

routing {
    authenticateWith(feedAuth) {
        get("/feed") {
            when (val user = call.principal) {
                is AuthenticatedUser ->
                    call.respondText(user.id)
                is GuestUser ->
                    call.respondText(user.label)
            }
        }
    }
}
```

## Transform a principal {id="map-principal"}

Use the `mapPrincipal()` function to convert the principal into another type, for example, by loading a user record from
your database:

```kotlin
data class AppUser(val id: String, val email: String)

val appAuth = jwtAuth.mapPrincipal { jwtUser ->
    userDirectory.find(jwtUser.id)?.let { row ->
        AppUser(id = row.id, email = row.email)
    }
}
```

The transform runs only after the scheme has produced a principal. If the transform returns `null`, the request is
rejected with `401 Unauthorized`.

## Check roles {id="roles"}

Role checks are opt-in. Define a role type that implements `AuthenticationRole`, then build a role-aware scheme with
`withRoles()`:

```kotlin
enum class Role : AuthenticationRole {
    User, Admin, Moderator
}

val roleAuth = jwtAuth.withRoles { user ->
    roleService.resolveRoles(user.id)
}
```

The `withRoles {}` block runs on every request, after authentication succeeds. Use it to load roles from a database, a
cache, or the principal itself.

Declare the roles a route requires with the `roles` parameter:

```kotlin
routing {
    authenticateWith(roleAuth, roles = setOf(Role.Admin)) {
        get("/admin") {
            val userRoles: Set<Role> = call.principal.roles
            call.respondText(userRoles.joinToString { it.name })
        }
    }
}
```

A caller who authenticates but lacks a required role receives a `403 Forbidden`. A caller must have every role in the set, not
just one of them.

Pass `roles = null` to resolve roles without requiring any. This is useful when the handler decides for itself:

```kotlin
authenticateWith(roleAuth, roles = null) {
    get("/dashboard") {
        if (Role.Admin in call.principal.roles) {
            call.respondText("admin view")
        } else {
            call.respondText("user view")
        }
    }
}
```

The `roles` property exists only inside a role-aware route. On a route protected by a plain scheme, it does not compile.

## Handle failures {id="failures"}

Two things can go wrong, and they are handled separately:

* `onUnauthorized` runs when authentication fails. The default response is `401 Unauthorized`.
* `onForbidden` runs when authentication succeeds but the caller lacks a required role. The default response is
  `403 Forbidden`.

Set a handler on the scheme to cover every route that uses it:

```kotlin
val jwtAuth = jwt<User>("my-jwt") {
    verifier(jwkProvider, issuer)
    validate { credential -> /* ... */ }
    onUnauthorized = { cause ->
        val message = cause.toString()
        call.respond(HttpStatusCode.Unauthorized, message)
    }
}

val roleAuth = jwtAuth.withRoles(
    onForbidden = {
        val forbidden = HttpStatusCode.Forbidden
        call.respondText("Not allowed", status = forbidden)
    }
) { user ->
    roleService.resolveRoles(user.id)
}
```

Or set a handler on a single route, which overrides the scheme-level one:

```kotlin
authenticateWith(
    roleAuth,
    roles = setOf(Role.Admin),
    onUnauthorized = { call.respondRedirect("/login") },
    onForbidden = {
        val forbidden = HttpStatusCode.Forbidden
        call.respondText("Admins only", status = forbidden)
    },
) {
    get("/admin") {
        call.respondText(call.principal.email)
    }
}
```

Ktor looks for an unauthorized handler in the following order:

1. The handler passed to `authenticateWith()`.
2. The `onUnauthorized` handler configured on the scheme.
3. The provider's default challenge.

If none of them responds, the request fails with `401 Unauthorized`.

For `authenticateWithAnyOf()`, the handler receives a map of scheme names to failure causes, so you can report why each
one failed:

```kotlin
authenticateWithAnyOf<AppUser>(
    jwtScheme,
    apiKeyScheme,
    onUnauthorized = { failures ->
        val schemes = failures.keys.joinToString()
        call.respond(HttpStatusCode.Unauthorized, schemes)
    }
) {
    get("/profile") { /* ... */ }
}
```

## Combine with the classic API {id="mixing"}

Both authentication APIs can be used in the same application. You can nest a type-safe route inside a route protected by
the named provider API, or nest a provider-based route inside a type-safe route:

```kotlin
routing {
    authenticate("auth-session") {
        authenticateWith(basicAuth) {
            get("/admin") {
                val basicUser = call.principal
                val sessionUser = 
                    checkNotNull(call.principal<SessionUser>())
                if (basicUser.name == sessionUser.name) {
                    call.respondText("You are ${basicUser.name}!")
                } else {
                    call.respondText("Who are you?")
                }
            }
        }
    }

    authenticate(basicAuth.name) {
        get("/hello") {
            val user = checkNotNull(call.principal<BasicUser>())
            call.respondText("Hello ${user.name}!")
        }
    }
}
```

Nested authentication layers are applied in order.

## Limitations {id="limitations"}

* The API is experimental. It may change in a minor release.
* The API uses Kotlin context parameters, which require Kotlin 2.4.0 or the `-Xcontext-parameters` compiler option.
* The `authenticateWithAnyOf()` function provides only `call.principal`. Scheme-specific properties such as `call.session`
  are not available inside it.
* There is no type-safe equivalent of the LDAP provider. Call
  [`ldapAuthenticate()`](server-ldap.md) inside a typed `validate {}` block instead.

[//]: # (title: OpenID Connect browser login)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<var name="artifact_name" value="ktor-server-auth-oidc"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>
Sign users in through an OpenID Connect provider. The plugin creates the login and callback routes and keeps users
signed in with a session.
</link-summary>

This topic covers signing users in from a browser. For an API that validates tokens somebody else issued, see
[](server-oidc-resource-server.md). For discovery and plugin setup, see [](server-oidc.md).

<include from="lib.topic" element-id="oidc_experimental"/>

## How the flow works {id="flow"}

1. The user opens `/oidc/{name}/login`.
2. Ktor redirects them to the provider, with a `state`, a `nonce`, and a PKCE challenge.
3. The user signs in at the provider and approves the requested scopes.
4. The provider sends them back to `/oidc/{name}/callback` with an authorization code.
5. Ktor exchanges the code for tokens, validates the ID token, and stores a session.
6. Your `onAuthenticated` handler runs.

You write step 6. The plugin creates these routes for you:

| Route                   | Method | Created                   |
|-------------------------|--------|---------------------------|
| `/oidc/{name}/login`    | `GET`  | Always                    |
| `/oidc/{name}/callback` | `GET`  | Always                    |
| `/oidc/{name}/logout`   | `POST` | When you call `logout()`  |
| `/oidc/{name}/refresh`  | `POST` | When you call `refresh()` |

Register the callback route with your provider as an allowed redirect URI.

## Sign users in {id="oauth"}

```kotlin
suspend fun Application.module() {
    val oidc = install(Oidc)

    val google = oidc.identityProvider("google") {
        issuer = "https://accounts.google.com"
        oauth {
            clientId = System.getenv("GOOGLE_CLIENT_ID")
            clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
            onAuthenticated { idToken ->
                val subject = idToken.userInfo.subject
                userService.recordLogin(subject)
                call.respondRedirect("/dashboard")
            }
        }
    }
}
```

`onAuthenticated` runs once, at the end of a successful callback, and the session is already stored by the time it runs.
Without it a successful login responds `200 OK` with an empty body, which is rarely what a browser user wants.

`scopes` defaults to `listOf("openid", "profile", "email")`. Assigning it **replaces** that list rather than adding to
it, and the result must still contain `openid`:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    scopes = listOf("openid", "email", "calendar.read")
}
```

Set `fetchUserInfo = true` if you need claims the provider keeps out of the ID token. This adds a request to the
UserInfo endpoint on every login.

Keep secrets out of a source. See [](server-oidc.md#config-file).

## Customize the routes {id="paths"}

All four generated paths can be moved. `loginUri` and `redirectUri` take a `URLBuilder` block, while `logout()` and
`refresh()` take a plain `path`:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    loginUri = { path("auth", "google", "signin") }
    redirectUri = { path("auth", "google", "callback") }
    logout(path = "/auth/google/signout")
    refresh(path = "/auth/google/renew")
}
```

`redirectUri` has to match a redirect URI registered with your provider, so change it there at the same time. None of
these builders may add query parameters; the plugin rejects a path that does.

## Protect routes with the session {id="session"}

`provider.session` authenticates users who are already signed in. Inside the block, `call.principal` is an
`OidcToken.Id`:

```kotlin
routing {
    authenticateWith(google.session) {
        get("/dashboard") {
            val user = call.principal.userInfo
            call.respondText("Hello ${user.name}")
        }
    }
}
```

Most applications map the token to their own user type. See [](server-oidc.md#map-principal):

```kotlin
data class AppUser(val id: String, val email: String?)

val sessionAuth = google.session.mapPrincipal { token ->
    val info = token.userInfo
    userService.find(info.subject) ?: AppUser(
        id = info.subject,
        email = info.email
    )
}
```

## Configure the session {id="sessions"}

Sessions are on by default. Use `sessions { }` to change the cookie name or the storage:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    sessions {
        name = "GOOGLE_SESSION"
        storage = directorySessionStorage(
            File("build/.sessions")
        )
        cookie {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }
}
```

The cookie name defaults to `{PROVIDER_NAME}_SESSION`. The plugin sets `HttpOnly`, `SameSite=Lax`, and `Secure`
outside development mode; override these only if you know why.

The transport is always `SessionTransportType.CookieId` and cannot be changed. Only a session id travels to the browser;
the ID token, access token, and refresh token stay server-side in `storage`.

Storage defaults to `SessionStorageMemory()`, which loses every session on restart and shares nothing between instances.
Configure real storage before you deploy. See [](server-sessions.md).

## Protect against CSRF {id="csrf"}

The routes the plugin generates are protected by origin checks:

```kotlin
sessions {
    csrfProtection {
        originMatchesHost()
    }
}
```

That is the default, so you only need this block to change it. `disableCsrfProtection()` exists, but the logout and
refresh routes accept `POST` from a browser that is carrying a session cookie, which is exactly what CSRF protection is
for.

## Sign users out {id="logout"}

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    logout(
        postLogoutRedirectUri = { path("signed-out") }
    )
}
```

`POST /oidc/google/logout` clears the session and responds `303 See Other`, pointing at the provider's
`end_session_endpoint` so the user is signed out there too.

The provider must advertise `end_session_endpoint` in its discovery document. This is checked when routes are
registered, so **a provider without it makes your application fail to start** rather than failing at sign-out time. If
your provider does not support it, clear the session yourself instead of calling `logout()`.

Pass a handler to run your own code, or to respond yourself instead of redirecting:

```kotlin
logout {
    call.respondRedirect("/goodbye")
}
```

Signing out does not revoke the refresh token at the provider.

## Keep sessions fresh {id="refresh"}

ID tokens expire. By default, the plugin does nothing about it: once the token is past `exp`, the session is cleared and
the user has to sign in again.

To refresh automatically, set a strategy:

```kotlin
sessions {
    tokenRefreshStrategy = OidcTokenRefreshStrategy.Auto(
        beforeExpiry = 30.seconds
    )
}
```

The refreshed token has to keep the same `sub`, or it is discarded.

`Auto` covers the usual case. For anything else, `Custom` hands you every session-authenticated request and lets you
decide:

```kotlin
sessions {
    val custom = OidcTokenRefreshStrategy.Custom { provider, token, now ->
        val expiresAt = token.claims.expiresAt
        val stale = expiresAt != null && expiresAt <= now + 5.minutes
        val refreshToken = token.refreshToken
        if (stale && refreshToken != null) {
            provider.refreshToken(refreshToken).idToken
        } else {
            token
        }
    }
    tokenRefreshStrategy = custom
}
```

Return `token` to leave the session as it is, a new `OidcToken.Id` to replace the stored session, or `null` when no
refreshed token is available. On `null`, and if you throw, the session is kept while the current token is still valid
and cleared once it has expired. To end a session immediately, clear it with the
[Sessions](server-sessions.md) plugin instead.

The callback runs on **every** session-authenticated request, so keep it cheap. Read the time from the `now`
parameter rather than the clock, so all checks within one request agree.

`now` and `claims.expiresAt` are `kotlin.time.Instant`, which is still experimental, so a strategy that compares them
needs `@OptIn(ExperimentalTime::class)` as well as the opt-in the plugin already requires.

You can also refresh on demand by adding the route:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    refresh()
}
```

`POST /oidc/google/refresh` responds `200 OK` on success and `401 Unauthorized` when the session cannot be refreshed. On
a 401 the existing session is left alone, so the user stays signed in until their token actually expires.

### Refresh it yourself {id="manual-refresh"}

For anything the route does not cover, such as a scheduled job or refreshing before calling a downstream API, call
`refreshToken()` on the provider:

```kotlin
val refreshToken = call.principal.refreshToken
if (refreshToken != null) {
    val result = google.refreshToken(refreshToken)
    val newIdToken = result.idToken
    if (newIdToken != null) {
        call.session = newIdToken
    }
}
```

`OidcTokenRefreshResult` carries the raw token response: `accessToken`, `refreshToken`, `expiresIn`, `tokenType`, and
`scope`. Its `idToken` is null when the provider did not return one, which is common, so check it before storing a
session.

Expect `ResponseException` when the provider rejects the request,
`OidcTokenRejectedException` when the returned tokens fail validation, and `OidcSigningKeyUnavailableException` when the
ID token cannot be verified. See [](server-oidc-resource-server.md#errors-500).

Concurrent calls with the same refresh token share a single request to the provider, and the result is reused for
`tokenRefreshCacheTtl` afterwards, so you do not need to coordinate this yourself.

> An ID token with no `exp` claim is never treated as expired and never refreshed. Such a session lasts as long as
> the cookie does.
>
{style="note"}

## Sign in without a session {id="no-session"}

If you manage your own session or issue your own token, turn the plugin's session off:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    disableSessions()
    onAuthenticated { idToken ->
        val token = myTokenService.issue(idToken.userInfo)
        call.respondText(token)
    }
}
```

`onAuthenticated` becomes required, because nothing else would happen otherwise. `provider.session`, `logout()`, and
`refresh()` are unavailable in this mode.

## Use several providers {id="multiple"}

Register one provider per issuer. Each gets its own routes, cookies, and schemes:

```kotlin
val google = oidc.identityProvider("google") {
    issuer = "https://accounts.google.com"
    oauth {
        clientId = System.getenv("GOOGLE_CLIENT_ID")
        clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    }
}

val github = oidc.identityProvider("github-idp") {
    issuer = "https://idp.example.com"
    oauth {
        clientId = System.getenv("IDP_CLIENT_ID")
        clientSecret = System.getenv("IDP_CLIENT_SECRET")
    }
}
```

Your login page links to `/oidc/google/login` and `/oidc/github-idp/login`. To let either provider open the same route,
map both to a shared principal type and use `authenticateWithAnyOf`.

## Request scoped tokens {id="resource-indicators"}

`resourceIndicators` ([RFC 8707](https://www.rfc-editor.org/rfc/rfc8707)) asks the provider for an access token that is
valid for a specific API, rather than one that works everywhere:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    resourceIndicators = listOf("https://api.example.com")
}
```

The value should match the identifier the API publishes as its
[protected resource metadata](server-oidc-resource-server.md#protected-resource).

## Why the code is exchanged with PKCE {id="pkce"}

The authorization code travels back through the user's browser, which means it passes through software you do not
control. Anything able to read that redirect, a malicious app registered for the same URL scheme or a logging proxy,
could take the code and try to redeem it for tokens.

PKCE ([RFC 7636](https://www.rfc-editor.org/rfc/rfc7636)) closes that hole. Before sending the user away, Ktor generates
a random secret called the **verifier** and sends only its SHA-256 hash, the **challenge**, to the provider. When Ktor
later exchanges the code, it presents the verifier, and the provider checks that it hashes to the challenge it stored. A
stolen code on its own is worthless, because whoever took it cannot produce the verifier.

The verifier is never sent to the provider up front, and the browser cannot read it. It is stored alongside the
`state` and `nonce` in the AES-256-GCM encrypted cookie the plugin sets, so the browser holds only ciphertext it has no
key for. Recovering it requires your `stateEncryptionKey`.

Ktor does this on every login and there is nothing to configure. `codeChallengeMethod` defaults to
`CodeChallengeMethod.S256`, and only `S256` is supported; custom methods are rejected. Because the verifier lives in
that cookie, the login has a time limit. See [](#errors-state).

## Handle sign-in errors {id="errors"}

### Respond to a failed login {id="errors-handler"}

`onAuthenticationFailed` runs when a callback cannot be completed:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    onAuthenticationFailed { cause ->
        val reason = (cause as? AuthenticationFailedCause.Error)
            ?.message
        call.application.log.info("Login failed: $reason")
        call.respondRedirect("/oidc/google/login")
    }
}
```

The handler receives an `AuthenticationFailedCause`, not an exception. The useful case is
`AuthenticationFailedCause.Error`, whose `message` is the only detail available.

Without a handler, a failed login responds with a bare `401` and empty body. That is a confusing thing for a user to hit
in a browser, so it is worth replacing. Sending them back to the login route restarts the flow cleanly, which is the
right outcome for the most common failure, described next.

Your handler should respond. If it does not, Ktor falls back to whatever challenge the failure registered, and otherwise
to the same bare `401`.

One failure behaves differently. When the token endpoint rejects the authorization code as `invalid_grant`, which
happens when a code has expired or was already used, a redirect back to the provider is registered as the fallback. A
handler that does not respond therefore restarts the login rather than showing an error.

That retry is usually what you want. A user who reloads the callback page sends the same code twice, and starting over
gets them a fresh one. The risk is that it repeats. If something makes *every* code fail, such as a wrong client secret
or a clock far enough out that codes look expired on arrival, each restart produces another failure and the user bounces
between your application and the provider.

Responding inside the handler is what prevents that, because the fallback never runs. If you want the retry anyway,
allow one and no more:

```kotlin
onAuthenticationFailed { cause ->
    val retried =
        call.request.cookies["oidc_retry"] != null
    if (cause is OAuth2InvalidGrantError && !retried) {
        call.response.cookies.append(
            name = "oidc_retry",
            value = "1",
            maxAge = 120,
            path = "/",
            httpOnly = true
        )
        call.respondRedirect("/oidc/google/login")
    } else {
        call.response.cookies.append(
            name = "oidc_retry",
            value = "",
            maxAge = 0,
            path = "/"
        )
        call.application.log.warn("Login failed: $cause")
        call.respond(HttpStatusCode.Unauthorized)
    }
}
```

A short-lived cookie marks that a retry already happened, so the second failure shows an error instead of redirecting
again. The cookie is cleared on that error, and expires by itself after two minutes so a later genuine login is not
affected.

### The login window is 10 minutes {id="errors-state"}

The `state`, `nonce`, and PKCE verifier live in an encrypted cookie that expires after 10 minutes. This is not
configurable.

A user who opens the login page, walks away, and comes back an hour later gets a failed callback. So does a user whose
login was in flight when you restarted the application, unless you configured `stateEncryptionKey`:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    stateEncryptionKey = OidcStateEncryptionKey.of(stateKey)
}
```

The key must be exactly 32 bytes. Without it the plugin generates one per process, so in-flight logins break on every
restart and fail outright behind a load balancer. `OidcStateEncryptionKey.rotating(current, previous)` lets you change
the key without breaking logins that are already under way.

An expired login and a forged callback produce the same failure, so the handler cannot tell them apart. This is why
redirecting back to the login route is the right default.

### What routes to the handler {id="errors-causes"}

| Cause                                  | Typical reason                                                                  |
|----------------------------------------|---------------------------------------------------------------------------------|
| The provider returned `error=`         | The user declined consent                                                       |
| The state cookie is missing or expired | The 10-minute window passed, or a restart without `stateEncryptionKey`          |
| The state cookie cannot be decrypted   | A forged callback, or a rotated key                                             |
| `iss` mismatch or missing              | A mix-up between providers ([RFC 9207](https://www.rfc-editor.org/rfc/rfc9207)) |
| `nonce` mismatch                       | A replayed ID token                                                             |
| `at_hash` mismatch                     | The ID token does not match the access token                                    |
| The response contains no `id_token`    | The provider is not running an OIDC flow                                        |
| ID token validation failed             | Signature, issuer, audience, `exp`, `iat`, `azp`, or `sub`                      |
| The token endpoint returned an error   | An expired or reused authorization code                                         |

### When the provider is unreachable {id="errors-500"}

Some failures never reach `onAuthenticationFailed` and surface as `500 Internal Server Error` instead, see
[](server-oidc-resource-server.md#errors-500).

The refresh cases are the ones to plan for here. With `OidcTokenRefreshStrategy.Auto` a refresh runs while handling an
ordinary request, so an unreachable provider turns your signed-in users' requests into 500s.

What happens to the session depends on why the refresh failed:

| Failure                                         | Session                                      | Result                                                            |
|-------------------------------------------------|----------------------------------------------|-------------------------------------------------------------------|
| The refreshed token is invalid                  | Cleared                                      | `401`, and the cookie is removed. The session could never recover |
| The provider is unreachable or returns an error | Kept, unless already expired                 | The exception propagates, so `500`                                |
| No ID token in the response, or `sub` changed   | Kept while still valid, cleared once expired | Works until the old token expires                                 |

A cleared session makes the next request unauthenticated, so the user goes back through login. That is the intended
outcome; the alternative is a session that fails on every request forever.

## What's next {id="next"}

* [](server-oidc.md) covers discovery, session security defaults, and testing.
* [](server-oidc-resource-server.md) covers validating tokens in an API.
* [](server-sessions.md) covers session storage and cookie configuration.

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

This topic explains how to use OpenID Connect to sign users in from a browser, manage their sessions, and handle logout
and token refresh.

> To validate tokens issued by an OpenID Connect provider, see[](server-oidc-resource-server.md).
> 
> For discovery and plugin setup, see [](server-oidc.md).
> 
{style="tip"}

<include from="lib.topic" element-id="oidc_experimental"/>

## How the flow works {id="flow"}

1. The user opens `/oidc/{name}/login`.
2. Ktor redirects the user to the provider, with a `state`, a `nonce`, and a PKCE challenge.
3. The user signs in with the provider and approves the requested scopes.
4. The provider sends them back to `/oidc/{name}/callback` with an authorization code.
5. Ktor exchanges the code for tokens, validates the ID token, and stores a session.
6. The `onAuthenticated` handler runs.

The plugin creates the following routes:

| Route                   | Method | Created                   |
|-------------------------|--------|---------------------------|
| `/oidc/{name}/login`    | `GET`  | Always                    |
| `/oidc/{name}/callback` | `GET`  | Always                    |
| `/oidc/{name}/logout`   | `POST` | When you call `logout()`  |
| `/oidc/{name}/refresh`  | `POST` | When you call `refresh()` |

Register the callback route with your provider as an allowed redirect URI.

## Sign users in {id="oauth"}

To handle a successful sign-in, configure OAuth credentials and use the `onAuthenticated` handler:

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

The `onAuthenticated` handler runs once at the end of a successful callback, after the session has been stored.
Without this handler, a successful login responds with `200 OK` and an empty body.

The `scopes` property defaults to `listOf("openid", "profile", "email")`. Assigning a value replaces the default list 
rather than adding to it, and the resulting list must still contain `openid`:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    scopes = listOf("openid", "email", "calendar.read")
}
```

Set `fetchUserInfo = true` to retrieve claims that the provider does not include in the ID token. This adds a request to
the UserInfo endpoint on every login.

> Store client secrets outside source code and in a configuration file. For more information, see [](server-oidc.md#config-file).
>
{style="tip"}

## Customize the routes {id="paths"}

You can customize all generated paths. The `loginUri` and `redirectUri` properties accept a `URLBuilder` block, while 
`logout()` and `refresh()` accept a `path`:

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

The `redirectUri` property must match a redirect URI registered with your provider. Update the registered redirect URI 
when you change this value.

None of these builders support query parameters. The plugin rejects paths that include them.

## Protect routes with the session {id="session"}

The `provider.session` scheme authenticates users with an existing session. Inside the block, `call.principal` is an
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

To work with an application-specific principal instead of the OIDC token, map the scheme with `mapPrincipal()`:

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

> For more information, see [](server-oidc.md#map-principal).
> 
{style="tip"}

## Configure the session {id="sessions"}

Sessions are enabled by default. Use the `sessions { }` block to configure the cookie name or session storage:

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
outside development mode. Override these settings only when required.

The transport is always `SessionTransportType.CookieId` and cannot be changed. Only the session ID is sent to the browser.
The ID token, access token, and refresh token remain server-side in `storage`.

Session storage defaults to `SessionStorageMemory()`, which loses all sessions when the application restarts and does not
share sessions between instances. Configure persistent or shared storage before deploying to production.

> For more information on working with sessions, see [](server-sessions.md).
> 
{style="tip"}

## Protect against CSRF {id="csrf"}

The routes generated by the plugin are protected by origin checks:

```kotlin
sessions {
    csrfProtection {
        originMatchesHost()
    }
}
```

This protection is enabled by default, so configure this block only when you need to change it.

You can disable CSRF protection with the `disableCsrfProtection()` function. However, the logout and
refresh routes accept `POST` requests from browsers that include the session cookie, so these routes should remain protected
against CSRF.

## Sign users out {id="logout"}

To sign users out, use the `logout()` function:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    logout(
        postLogoutRedirectUri = { path("signed-out") }
    )
}
```

A `POST` request to `/oidc/google/logout` clears the session and responds with `303 See Other`, redirecting the user to
the provider's `end_session_endpoint`.

The provider must advertise `end_session_endpoint` in its discovery document. Ktor checks this when routes are
registered, so a provider without it makes your application fail to start rather than failing at sign-out time. If
your provider does not support this endpoint, clear the session directly instead of calling `logout()`.

Pass a handler to run additional logic or provide a custom response instead of redirecting:

```kotlin
logout {
    call.respondRedirect("/goodbye")
}
```

Signing out does not revoke the refresh token at the provider.

## Keep sessions fresh {id="refresh"}

ID tokens expire. By default, the plugin does not refresh them. Once the token is past its `exp` value, the session is
cleared and the user must sign in again.

To refresh tokens automatically, configure a refresh strategy:

```kotlin
sessions {
    tokenRefreshStrategy = OidcTokenRefreshStrategy.Auto(
        beforeExpiry = 30.seconds
    )
}
```

The refreshed token must have the same `sub` value as the existing token. Otherwise, the refreshed token is discarded.

For automatic refreshes, use `OidcTokenRefreshStrategy.Auto`. For custom refresh behavior, use `OidcTokenRefreshStrategy.Custom`:

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

Return `token` to keep the existing session, a new `OidcToken.Id` to replace the stored session, or `null` when no
refreshed token is available. If the callback returns `null` or throws an exception, the session remains available while
the current token is valid and is cleared once it expires. To end a session immediately, clear it with the
[Sessions](server-sessions.md) plugin instead.

The callback runs for every request authenticated with the session scheme, so keep its work lightweight. Use the `now`
parameter instead of reading the clock directly, so all time comparisons within the request use the same value.

The `now` parameter and `claims.expiresAt` are `kotlin.time.Instant`, which is experimental. A strategy that compares them
needs `@OptIn(ExperimentalTime::class)` in addition to the opt-in required by the plugin.

You can also add a route for on-demand refresh:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    refresh()
}
```

A `POST` request to `/oidc/google/refresh` responds with `200 OK` on success and `401 Unauthorized` when the session 
cannot be refreshed. On a `401` response, the existing session remains unchanged until its token expires.

### Refresh tokens manually {id="manual-refresh"}

For cases that do not use the generated refresh route, such as a scheduled job or a refresh before calling a downstream
API, call the `refreshToken()` function on the provider:

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
`scope`. The `idToken` property is null when the provider does not return an ID token, so check it before storing a
session.

The provider can throw the following exceptions during refresh:
* `ResponseException` when the provider rejects the request.
* `OidcTokenRejectedException` when the returned tokens fail validation.
* `OidcSigningKeyUnavailableException` when the ID token cannot be verified. For more information, see [](server-oidc-resource-server.md#errors-500).

Concurrent calls with the same refresh token share a single request to the provider. The result is reused for
`tokenRefreshCacheTtl`, so no additional synchronization is required.

> An ID token without an `exp` claim is never treated as expired or refreshed. The session remains valid for as long as
> the cookie remains valid.
>
{style="note"}

## Sign in without a session {id="no-session"}

If you manage your own session or issue your own token, disable the plugin's session support:

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

When sessions are disabled, `onAuthenticated` is required. The `provider.session`, `logout()`, and
`refresh()` functions are not available in this mode.

## Use multiple providers {id="multiple"}

Register one identity provider for each issuer. Each provider has its own routes, cookies, and authentication schemes:

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

In the above example, the login page links to `/oidc/google/login` and `/oidc/github-idp/login`. To allow either provider
to authenticate the same route, map both to a shared principal type and use the `authenticateWithAnyOf()` function.

## Request scoped tokens {id="resource-indicators"}

Use the `resourceIndicators` property ([RFC 8707](https://www.rfc-editor.org/rfc/rfc8707)) to request an access token for a specific API:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    resourceIndicators = listOf("https://api.example.com")
}
```

The value should match the identifier the API publishes in its
[protected resource metadata](server-oidc-resource-server.md#protected-resource).

## Understand PKCE {id="pkce"}

The authorization code returns through the user's browser and passes through software outside your
control. Another application, such as a malicious app registered for the same URL scheme or a logging proxy,
could get the code and attempt to redeem it for tokens.

PKCE ([RFC 7636](https://www.rfc-editor.org/rfc/rfc7636)) prevents the authorization code from being used without an
additional secret. Before redirecting the user, Ktor generates
a random secret called the _verifier_ and sends only its SHA-256 hash, the _challenge_, to the provider. When Ktor
later exchanges the code, it presents the verifier, and the provider checks that it hashes to the challenge it stored.

The verifier is not sent to the provider during the authorization request and cannot be read by browser scripts. Ktor stores
it alongside the `state` and `nonce` values in the AES-256-GCM encrypted cookie the plugin sets, so the browser holds
only ciphertext it has no key for. Decrypting the cookie requires your `stateEncryptionKey`.

Ktor uses PKCE for every login. The `codeChallengeMethod` defaults to
`CodeChallengeMethod.S256`, and only `S256` is supported. Custom challenge methods are rejected. Because the verifier 
is stored in that cookie, the login has [a time limit](#errors-state).

## Handle sign-in errors {id="errors"}

### Respond to a failed login {id="errors-handler"}

The `onAuthenticationFailed` handler runs when a callback cannot be completed:

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

The handler receives an `AuthenticationFailedCause`, not an exception.
`AuthenticationFailedCause.Error` provides a `message` with details about the failure.

Without a handler, a failed login responds with a `401 Unauthorized` and an empty body. For browser applications, you can
redirect the user to the login route to start a new authentication flow.

The handler should produce a response. If it does not, Ktor falls back to the challenge the failure registered, or responds
with a `401 Unauthorized` when no challenge is available.

When the token endpoint rejects the authorization code as `invalid_grant`, which
happens when a code has expired or has already been used, a redirect back to the provider is registered as the fallback.
If the handler does not respond, the login flow starts again.

Restarting the flow is useful when, for example, a user reloads the callback page and attempts to reuse the same code.
The risk is that it repeats. However, repeated invalid_grant responses can create a redirect loop. This can happen when every authorization code
fails, for example because the client secret is incorrect or the system clock causes codes to appear expired.

To prevent the fallback redirect, respond inside the handler. To allow a single retry, track whether a retry
has already occurred:

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

A short-lived cookie records whether a retry has already occurred. A second failure returns an error instead of redirecting
again. The cookie is cleared on that error, and expires automatically after two minutes so the next login is not
affected.

### The login window is 10 minutes {id="errors-state"}

The `state`, `nonce`, and PKCE verifier live in an encrypted cookie that expires after 10 minutes. This is not
configurable.

A user who opens the login page, walks away, and comes back an hour later gets a failed callback. So does a user whose
login was in progress when the application , unless you configured `stateEncryptionKey`:

```kotlin
oauth {
    clientId = System.getenv("GOOGLE_CLIENT_ID")
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
    stateEncryptionKey = OidcStateEncryptionKey.of(stateKey)
}
```

The key must be exactly 32 bytes. Without an explicit key, the plugin generates a new key for each process. As a result,
in-progress logins fail after an application restart and cannot continue across instances behind a load balancer. You can
use `OidcStateEncryptionKey.rotating(current, previous)` to change the key without invalidating login flows that are
already in progress.

An expired login and a forged callback produce the same failure, so the handler cannot distinguish between them. This is
why redirecting back to the login route is the default.

### Authentication failure causes {id="errors-causes"}

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

### Handle provider availability errors {id="errors-500"}

Some failures never reach `onAuthenticationFailed` and surface as `500 Internal Server Error` instead. For more information,
see [](server-oidc-resource-server.md#errors-500).

This can occur during automatic token refresh. With `OidcTokenRefreshStrategy.Auto` a refresh runs while handling an
ordinary request. If the provider is unavailable, the request can therefore fail with a `500` response.

What happens to the session depends on why the refresh failed:

| Failure                                         | Session                                      | Result                                                            |
|-------------------------------------------------|----------------------------------------------|-------------------------------------------------------------------|
| The refreshed token is invalid                  | Cleared                                      | `401`, and the cookie is removed. The session could never recover |
| The provider is unreachable or returns an error | Kept, unless already expired                 | The exception propagates, so `500`                                |
| No ID token in the response, or `sub` changed   | Kept while still valid, cleared once expired | Works until the old token expires                                 |

After a session is cleared, the next protected request is unauthenticated and the user must sign in again.

> To learn more about discovery, session security defaults, and testing, see [](server-oidc.md).
> 
> For validating tokens in an API, see [](server-oidc-resource-server.md).
> 
> For session storage and cookie configuration, see[](server-sessions.md).
> 
{style="tip"}

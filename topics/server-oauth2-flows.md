[//]: # (title: OAuth 2.0 flows)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>, <code>io.ktor:ktor-server-sessions</code>
</p>
</tldr>

<link-summary>
Typed OAuth 2.0 flows install their own login and callback routes, and can keep users signed in through a typed
session.
</link-summary>

Ktor provides two typed OAuth 2.0 flows as part of the [type-safe authentication API](server-typed-auth.md):

* `oauth2Session` signs the user in, stores a session, and gives you a scheme for protecting your application
  routes. Use this one unless you have a reason not to.
* `oauth2` signs the user in and hands you the token in the callback. It does not protect later routes.

A flow is not a scheme you pass to `authenticateWith()`. It is a value you `install()` into your routing, and it
creates the login and callback routes for you.

<include from="lib.topic" element-id="typed_auth_experimental"/>

> This topic covers the typed API. For the classic `oauth` provider, see [](server-oauth.md).
>
{style="note"}

## How the flow works {id="flow"}

1. The user visits the login path. Ktor redirects them to the OAuth provider.
2. The user approves the requested permissions at the provider.
3. The provider redirects back to your callback path with an authorization code.
4. Ktor exchanges the code for an access token.
5. For `oauth2Session`, Ktor stores a session and resolves a principal, then runs your callback handler.

## Sign users in with oauth2Session {id="oauth2-session"}

A flow needs an [HttpClient](client-create-and-configure.md) to call the provider's token endpoint, so add a client
engine dependency such as `io.ktor:ktor-client-cio` as well.

Declare two types: the principal your routes work with, and the session Ktor stores for the caller.

```kotlin
data class User(val id: String, val email: String)
data class UserSession(val accessToken: String)
```

Then create the flow. Note the order of the type arguments: the principal comes first, the session second.

```kotlin
val googleAuthorizeUrl =
    "https://accounts.google.com/o/oauth2/auth"
val googleTokenUrl = "https://oauth2.googleapis.com/token"
val profileScope =
    "https://www.googleapis.com/auth/userinfo.profile"

val googleAuth = oauth2Session<User, UserSession>("google") {
    client = HttpClient(CIO)
    settings = OAuthServerSettings.OAuth2ServerSettings(
        name = "google",
        authorizeUrl = googleAuthorizeUrl,
        accessTokenUrl = googleTokenUrl,
        requestMethod = HttpMethod.Post,
        clientId = System.getenv("GOOGLE_CLIENT_ID"),
        clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
        defaultScopes = listOf(profileScope)
    )
    loginPath = "/login"
    callback("/callback") {
        call.respondRedirect("/profile")
    }
    sessions {
        sessionCreator = { token ->
            UserSession(token.accessToken)
        }
        validate { session ->
            userService.loadUser(session.accessToken)
        }
    }
}
```

The `sessions` block does two jobs:

* `sessionCreator` turns the token response into the session you want to store. It runs once, right after the token
  exchange.
* `validate` turns a stored session into a principal. It runs on every request to a protected route.

Both are required.

Install the flow and protect your routes with `flow.session`:

```kotlin
routing {
    install(googleAuth)

    authenticateWith(googleAuth.session) {
        get("/profile") {
            val user = call.principal
            val session = call.session
            val token = session.accessToken
            call.respondText("${user.email} ($token)")
        }
    }
}
```

`install()` creates the login route, creates the callback route, and installs the [Sessions](server-sessions.md)
plugin for the session scheme. You do not install `Sessions` yourself.

Inside the protected routes, `call.principal` is your `User` and `call.session` is your `UserSession`. See
[](server-typed-session-auth.md) for everything you can do with the session.

## Protected routes do not redirect {id="redirects"}

The routes the flow creates for you, the login path and the callback path, redirect unauthenticated visitors to the
OAuth provider. Routes you protect with `authenticateWith(flow.session)` **do not**. A request without a valid
session gets `401 Unauthorized`.

You have two ways to send users to the provider:

* Link to `loginPath` from your sign-in page.
* Redirect there yourself when a session is missing:

  ```kotlin
  val googleAuth = oauth2Session<User, UserSession>("google") {
      // ...
      sessions {
          sessionCreator = { token ->
              UserSession(token.accessToken)
          }
          validate { session ->
              userService.loadUser(session.accessToken)
          }
          onUnauthorized = { call.respondRedirect("/login") }
      }
  }
  ```

## Handle sign-in failures {id="errors"}

Two handlers cover the two ways sign-in can fail.

`onUnauthorized` on the flow handles OAuth errors, such as the user declining consent, the token exchange
failing, `sessionCreator` or `validate` returning `null`:

```kotlin
val googleAuth = oauth2Session<User, UserSession>("google") {
    // ...
    onUnauthorized = { cause ->
        call.respondRedirect("/login?error=${cause}")
    }
}
```

## Use several providers {id="provider-lookup"}

Set `providerLookup` instead of `settings` to choose the provider per request. The block runs in the routing
context, so you can read the path, query parameters, or headers:

```kotlin
val socialAuth = oauth2Session<User, UserSession>("social") {
    client = HttpClient(CIO)
    providerLookup = {
        when (call.parameters["provider"]) {
            "google" -> googleSettings
            "github" -> githubSettings
            else -> null
        }
    }
    loginPath = "/login/{provider}"
    callback("/callback/{provider}") {
        call.respondRedirect("/profile")
    }
    sessions {
        sessionCreator = { token ->
            UserSession(token.accessToken)
        }
        validate { session ->
            userService.loadUser(session.accessToken)
        }
    }
}
```

Set either `settings` or `providerLookup`, not both.

## Sign in without a session {id="oauth2"}

Use `oauth2` when you do not need Ktor to protect later routes, for example, when you issue your own JWT after
sign-in. The callback handler receives the token response:

```kotlin
val githubAuthorizeUrl =
    "https://github.com/login/oauth/authorize"
val githubTokenUrl =
    "https://github.com/login/oauth/access_token"

val githubOAuth = oauth2("github") {
    client = HttpClient(CIO)
    settings = OAuthServerSettings.OAuth2ServerSettings(
        name = "github",
        authorizeUrl = githubAuthorizeUrl,
        accessTokenUrl = githubTokenUrl,
        requestMethod = HttpMethod.Post,
        clientId = System.getenv("GITHUB_CLIENT_ID"),
        clientSecret = System.getenv("GITHUB_CLIENT_SECRET")
    )
    loginPath = "/login"
    callback("/callback") { token ->
        val jwt = tokenService.issueToken(token.accessToken)
        call.respondText(jwt)
    }
}

routing {
    install(githubOAuth)
}
```

There is no `flow.session` here, so there is nothing to pass to `authenticateWith()`. Protect your routes with
another scheme, such as [`jwt`](server-jwt.md).

## Security notes {id="security"}

* Keep `clientSecret` out of source control. Read it from an environment variable or your
  [configuration file](server-configuration-file.topic).
* The callback stores the session under a newly generated session ID and discards any session ID that arrived with
  the callback request. This prevents session fixation.
* The default session transport keeps session data on the server and sends only an ID to the client. If you switch
  to a by-value transport, add a transformer. See [](server-typed-session-auth.md#transport).

## What's next {id="next"}

* [](server-typed-auth.md) covers roles, optional authentication, and the rest of the API.
* [](server-typed-session-auth.md) covers sessions, transports, and signing users out.

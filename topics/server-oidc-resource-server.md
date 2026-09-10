[//]: # (title: OpenID Connect resource server)

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
Validate access tokens issued by an OpenID Connect provider, either locally as JWTs or through token introspection.
</link-summary>

A resource server is an API that accepts tokens somebody else issued. It has no login page and no cookies. A client
sends an access token, and the server decides whether to trust it.

This topic covers that case. For browser sign-in, see [](server-oidc-browser-login.md). For discovery, token types, and
plugin setup, see [](server-oidc.md).

<include from="lib.topic" element-id="oidc_experimental"/>

## Validate JWT access tokens {id="jwt-bearer"}

Most providers issue access tokens as signed JWTs. Configure the audience your API expects, then protect routes with
`provider.jwtBearer`:

```kotlin
suspend fun Application.module() {
    val oidc = install(Oidc)

    val auth0 = oidc.identityProvider("auth0") {
        issuer = "https://my-tenant.auth0.com"
        bearer {
            audience = setOf("https://api.example.com")
        }
    }

    routing {
        authenticateWith(auth0.jwtBearer) {
            get("/orders") {
                val subject = call.principal.claims.subject
                call.respondText("Hello $subject")
            }
        }
    }
}
```

`audience` is required and must not be empty. It is the identifier your API is known by at the provider, which is
usually a different string as your client ID.

The plugin reads the token from the `Authorization: Bearer` header, fetches the signing key from the provider's JWKS
endpoint, and checks the signature, issuer, audience, and expiry. Nothing else is needed.

## Validate opaque tokens {id="introspection"}

Not every provider issues JWTs. An **opaque** token is a random string with no readable content, so your server cannot
check it on its own. That is a deliberate trade-off: an opaque token leaks nothing if it is intercepted, and because the
provider is consulted on every use it can be revoked and stop working immediately. A JWT stays valid until it expires,
whatever happens at the provider.

Asking the provider is standardized as **token introspection**,
[RFC 7662](https://www.rfc-editor.org/rfc/rfc7662). Your server posts the token to an introspection endpoint,
authenticating as itself, and the provider answers with `active: true` or `false` plus whatever metadata it chooses to
share, such as the subject, scope, client, and expiry.

Add an `introspection { }` block and use `provider.introspectionBearer`:

```kotlin
val auth0Issuer = "https://my-tenant.auth0.com"

val auth0 = oidc.identityProvider("auth0") {
    issuer = auth0Issuer
    bearer {
        audience = setOf("https://api.example.com")
        introspection {
            endpoint = "$auth0Issuer/oauth/introspect"
            clientId = "api-client"
            clientSecret = System.getenv("INTROSPECTION_SECRET")
        }
    }
}

routing {
    authenticateWith(auth0.introspectionBearer) {
        get("/orders") {
            val result = call.principal.introspection
            call.respondText("Hello ${result.username}")
        }
    }
}
```

The introspection endpoint is **not** part of the OpenID Connect discovery document, so the plugin cannot find it for
you. Look it up in your provider's documentation and set it explicitly. Note that the issuer URL is held in a local
value: inside `introspection { }` the outer `issuer` property is out of scope.

The `clientId` and `clientSecret` here identify **your API** to the provider, not the user. Introspection is a
privileged operation, so most providers require a separate client that is allowed to perform it. The plugin
authenticates with HTTP Basic by default; set `authMethod = ClientAuthenticationMethod.ClientSecretPost` to send the
credentials in the form body instead.

A token is accepted only when the response says `active: true`, its audience intersects your configured `audience`, and
any `iss`, `exp`, and `nbf` it returns check out.

The cost is a network round trip on every request, and your API stops working when the provider does. Prefer
[JWT validation](#jwt-bearer) when your provider issues JWTs, and use introspection when it does not, or when immediate
revocation matters more than latency.

## Read the token from somewhere else {id="token-extractor"}

If your clients do not use the `Authorization` header, supply your own extractor:

```kotlin
bearer {
    audience = setOf("https://api.example.com")
    tokenExtractor = { call.request.cookies["access_token"] }
}
```

Return `null` when there is no token. The request then fails as unauthenticated.

## Publish protected resource metadata {id="protected-resource"}

[RFC 9728](https://www.rfc-editor.org/rfc/rfc9728) lets a client discover which authorization servers your API trusts,
instead of being configured with them ahead of time. This matters for machine-to-machine clients and for MCP servers.

```kotlin
val oidc = install(Oidc) {
    protectedResource("https://api.example.com") {
        resourceName = "Orders API"
    }
}
```

This serves a document at `/.well-known/oauth-protected-resource`. Most of it is filled in from the providers you
registered:

* `authorizationServers` — the issuers of every provider that has a `bearer { }` block
* `scopesSupported` — the scopes those providers request
* `bearerMethodsSupported` — `header`, when a provider reads the standard header

Set any of these explicitly to override what was derived.

Configuring a protected resource also changes the challenge. Instead of a bare `WWW-Authenticate: Bearer`, rejected
requests get a pointer to the metadata document:

```http
WWW-Authenticate: Bearer resource_metadata="https://api.example.com/.well-known/oauth-protected-resource"
```

## Handle authentication errors {id="errors"}

### What a client sees {id="errors-response"}

A rejected token produces `401 Unauthorized` with a `WWW-Authenticate: Bearer` header.

The challenge carries no `error` or `error_description` parameter and no `realm`. A client cannot tell a missing token
from an expired one, or a bad signature from a wrong audience. This is deliberate: the details would tell an attacker
which part of a forged token to fix. It also means **you** cannot debug from the response either — see
[](#errors-logging).

These all produce a plain 401:

| Cause                                              | Notes                                              |
|----------------------------------------------------|----------------------------------------------------|
| No token, or a malformed `Authorization` header    |                                                    |
| Bad signature                                      |                                                    |
| Wrong issuer or wrong audience                     |                                                    |
| Expired token                                      | After the `clockSkew` leeway                       |
| `none`, `HS256`, `HS384`, or `HS512` algorithm     | Always rejected                                    |
| An algorithm outside `allowedAlgorithms`           |                                                    |
| Unknown `kid`                                      | The key is not in a JWKS document that was fetched |
| Introspection returned `active: false`             |                                                    |
| Introspection returned a wrong or expired audience |                                                    |

An unknown `kid` is a statement about the token, so it is a 401. A `kid` that cannot be looked up **at all** is a
different thing, covered next.

### When the server returns 500 {id="errors-500"}

Some failures say nothing about the caller's token. The plugin could not complete a check, which is the server's problem
and not the caller's. Answering `401` would tell the caller their token is invalid, which is not something the server
has established. These surface as `500 Internal Server Error` instead:

| Failure                                                                    | Exception                                                      |
|----------------------------------------------------------------------------|----------------------------------------------------------------|
| The JWKS endpoint is unreachable                                           | `OidcSigningKeyUnavailableException`                           |
| The JWKS document cannot be parsed                                         | `OidcSigningKeyUnavailableException`                           |
| The JWKS request rate limit is exhausted                                   | `OidcSigningKeyUnavailableException`                           |
| The introspection endpoint is unreachable or answers with a non-2xx status | `ResponseException`, `IOException`, or a deserialization error |
| A `fetchUserInfo` request fails at the transport level                     | As above                                                       |
| A session token refresh fails at the transport level                       | As above. See [](server-oidc-browser-login.md#errors)          |

The practical consequence is that **an outage at your identity provider turns authenticated requests into 500s**, which
is worth knowing before it happens at 3am.

The rate limit deserves particular attention, because it is on by default and easy to trip. Key lookups are limited to
10 per minute, and a lookup for a `kid` that is not in the cache counts against it. A burst of tokens signed with
unknown keys, which is what a key rotation looks like, can exhaust the bucket. Raise it with
`jwt { jwkRateLimit(bucketSize = 60) }` if you see this.

Install [](server-status-pages.md) to answer with something better than `500`:

```kotlin
install(StatusPages) {
    exception<OidcSigningKeyUnavailableException> { call, cause ->
        val log = call.application.log
        log.error("Signing key unavailable", cause)
        call.respond(HttpStatusCode.ServiceUnavailable)
    }
}
```

`503` is the better answer because the request is likely to succeed once the provider recovers.

Only the signing-key failure has a dedicated exception type, and it is the only one worth catching globally like this.
The rest surface as `ResponseException` or `IOException`, which are **not** specific to this plugin. Any
[HTTP client](client-create-and-configure.md) call anywhere in your application throws those same types, so a global
handler would swallow unrelated failures and report them as a provider outage. Handle those nearer to where they happen,
or leave them as `500`.

### Find out why a token was rejected {id="errors-logging"}

The reason is logged at `TRACE` level, and nowhere else. Turn it on for the plugin's package in your
[logger configuration](server-logging.md#configure-logger), which for Logback is `logback.xml` in the root of the
classpath, usually `src/main/resources/logback.xml`:

```xml

<configuration>
    <logger name="io.ktor.server.auth.oidc" level="TRACE"/>
</configuration>
```

Each provider logs under `io.ktor.server.auth.oidc.OidcProvider[<name>]`, so you can raise the level for a single
provider without the noise from the rest.

Messages name the specific check that failed, for example
`JWT algorithm HS256 is not accepted` or `JWT kid abc123 does not match any JWK`.

### Customize the 401 {id="errors-custom"}

There is no failure handler inside `bearer { }`. Set one on the route instead:

```kotlin
routing {
    authenticateWith(
        auth0.jwtBearer,
        onUnauthorized = { cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("error" to "invalid_token")
            )
        }
    ) {
        get("/orders") {
            call.respondText("ok")
        }
    }
}
```

A route-level handler replaces the built-in response completely, including the `WWW-Authenticate` header. If you
configured [protected resource metadata](#protected-resource), the `resource_metadata` hint disappears with it, so add
the header back yourself if clients rely on it.

Use `authenticateWithAnyOf(..., onUnauthorized = ...)` for the multi-scheme form.

### Do not reuse your client ID as the audience {id="audience-overlap"}

If `bearer { audience }` contains the `clientId` from your `oauth { }` block, an ID token issued for login can pass as
an access token for your API, unless the provider marks it with a `token_use` or `typ` claim. Not all providers do.

Give your API its own resource identifier:

```kotlin
oidc.identityProvider("auth0") {
    issuer = "https://my-tenant.auth0.com"
    bearer {
        // A resource identifier, not the login client ID
        audience = setOf("https://api.example.com")
    }
    oauth {
        clientId = "web-client"
        clientSecret = System.getenv("WEB_CLIENT_SECRET")
    }
}
```

The plugin logs a warning at startup when it spots this overlap.

## What's next {id="next"}

* [](server-oidc.md) covers discovery, token validation settings, and testing.
* [](server-oidc-browser-login.md) covers browser sign-in with the same provider.

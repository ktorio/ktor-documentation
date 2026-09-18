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

A resource server is an API that accepts tokens somebody else issued. It does not provide a login page or
use browser sessions. A client sends an access token, and the server validates it before allowing access to protected
resources.

> For browser sign-in, see [](server-oidc-browser-login.md).
> 
> For discovery, token types, and plugin setup, see [](server-oidc.md).
> 
{style="tip"}

<include from="lib.topic" element-id="oidc_experimental"/>

## Validate JWT access tokens {id="jwt-bearer"}

Many providers issue access tokens as signed JSON Web Tokens (JWTs). Configure the audience your API expects, then
protect routes with `provider.jwtBearer`:

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

The `audience` property is required and must not be empty. It identifies your API at the provider and is usually
different from the OAuth client ID.

The plugin reads the token from the `Authorization: Bearer` header, retrieves the signing key from the provider's JWKS
endpoint, and checks the signature, issuer, audience, and expiry.

## Validate opaque tokens {id="introspection"}

Not all providers issue JWTs. An _opaque_ token is a random string with no readable content, so your server cannot
validate it locally. That is a deliberate trade-off: an opaque token leaks nothing if it is intercepted, and because the
provider is consulted on every use, it can be revoked and stop working immediately. A JWT stays valid until it expires.

To validate an opaque token, the server uses _token introspection_, as defined by 
[RFC 7662](https://www.rfc-editor.org/rfc/rfc7662). Your server sends the token to an introspection endpoint,
authenticates itself, and receives a response that indicates whether the token is active and any relevant metadata, such
as the subject, scope, client, and expiry.

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

The introspection endpoint is not part of the OpenID Connect discovery document, so configure it explicitly, according to
the provider's documentation. In the example above, the issuer URL is stored in a local variable because the outer `issuer`
property is not available in the `introspection { }` block.

The `clientId` and `clientSecret` properties identify your API to the provider, not the user. Introspection is a
privileged operation, so most providers require a separate client that is allowed to perform it. 

By default, the plugin uses HTTP Basic authentication. To send the credentials in the form body instead, set
`authMethod = ClientAuthenticationMethod.ClientSecretPost`.

A token is accepted only when the response contains `active: true`, its audience intersects your configured `audience`, and
any `iss`, `exp`, and `nbf` values it returns are valid.

Introspection requires a network request for each authentication attempt and depends on provider availability. Prefer
[JWT validation](#jwt-bearer) when your provider issues JWTs, and use introspection when it does not, or when immediate
revocation matters more than latency.

## Read the token from another location {id="token-extractor"}

By default, the plugin reads the token from the `Authorization` header. To read it from another location, configure a
custom extractor:

```kotlin
bearer {
    audience = setOf("https://api.example.com")
    tokenExtractor = { call.request.cookies["access_token"] }
}
```

Return `null` when no token is available. The request then fails as unauthenticated.

## Publish protected resource metadata {id="protected-resource"}

[RFC 9728](https://www.rfc-editor.org/rfc/rfc9728) lets a client discover which authorization servers your API trusts,
instead of being configured with them ahead of time. This can be useful for machine-to-machine clients and MCP servers.

Configure protected resource metadata when installing the plugin:

```kotlin
val oidc = install(Oidc) {
    protectedResource("https://api.example.com") {
        resourceName = "Orders API"
    }
}
```

This serves a document at `/.well-known/oauth-protected-resource`.The plugin derives the following values
from registered providers:

* `authorizationServers` — the issuers of every provider that has a `bearer { }` block.
* `scopesSupported` — the scopes those providers request.
* `bearerMethodsSupported` — `header`, when a provider reads the standard authorization header.

Set any of these properties explicitly to override the derived value.

Configuring a protected resource also changes the challenge. Instead of a `WWW-Authenticate: Bearer` header, rejected
requests include a pointer to the metadata document:

```http
WWW-Authenticate: Bearer resource_metadata="https://api.example.com/.well-known/oauth-protected-resource"
```

## Handle authentication errors {id="errors"}

### Authentication error responses {id="errors-response"}

A rejected token produces `401 Unauthorized` with a `WWW-Authenticate: Bearer` header.

The challenge does not include an `error`, `error_description`, and `realm` parameter. As a result, a client cannot
distinguish between a missing token and an expired one, or a bad signature from a wrong audience. For details about
diagnosing rejected tokens, see [](#errors-logging).

The following conditions produce `401 Unauthorized`:

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

An unknown `kid` indicates that the token references a key that is not present in the retrieved JWKS document and
therefore results in `401 Unauthorized`.

### Handle server-side validation failures {id="errors-500"}

Some failures prevent the plugin from completing token validation without establishing that the token itself is invalid.
These failures result in `500 Internal Server Error`:

| Failure                                                                    | Exception                                                      |
|----------------------------------------------------------------------------|----------------------------------------------------------------|
| The JWKS endpoint is unreachable                                           | `OidcSigningKeyUnavailableException`                           |
| The JWKS document cannot be parsed                                         | `OidcSigningKeyUnavailableException`                           |
| The JWKS request rate limit is exhausted                                   | `OidcSigningKeyUnavailableException`                           |
| The introspection endpoint is unreachable or answers with a non-2xx status | `ResponseException`, `IOException`, or a deserialization error |
| A `fetchUserInfo` request fails at the transport level                     | As above                                                       |
| A session token refresh fails at the transport level                       | As above. See [](server-oidc-browser-login.md#errors)          |

An identity provider outage can therefore cause authenticated requests to fail with
`500 Internal Server Error`.

JWK request rate limiting is enabled by default and can also cause signing-key lookup failures. Key lookups are limited
to 10 requests per minute, and a lookup for a kid that is not present in the cache counts toward the limit. A burst of
tokens signed with previously unseen keys, such as during key rotation, can exhaust the limit.

To increase the limit, configure `jwkRateLimit`:

```kotlin
jwt { 
    jwkRateLimit(bucketSize = 60)
}
```

To return a more appropriate response, install the [`StatusPages`](server-status-pages.md) plugin:

```kotlin
install(StatusPages) {
    exception<OidcSigningKeyUnavailableException> { call, cause ->
        val log = call.application.log
        log.error("Signing key unavailable", cause)
        call.respond(HttpStatusCode.ServiceUnavailable)
    }
}
```

`503 Service Unavailable` indicates that the request may succeed after the identity provider recovers.

Only the signing-key failure has a dedicated exception type, and it is the only one worth catching globally like this.
The rest surface as `ResponseException` or `IOException`, which are also used by other
[HTTP client](client-create-and-configure.md) operations. Avoid handling these exception types globally
as this could result in unrelated failures being reported as provider outages. Handle them close to the operation that
can produce them, or allow them to result in `500 Internal Server Error`.

### Find out why a token was rejected {id="errors-logging"}

Token rejection details are logged at the ` TRACE ` level. To enable them, configure logging for the plugin package. With
Logback, add the following to <path>logback.xml</path>, typically located at <path>src/main/resources/logback.xml</path>:

```xml

<configuration>
    <logger name="io.ktor.server.auth.oidc" level="TRACE"/>
</configuration>
```

Each provider logs under `io.ktor.server.auth.oidc.OidcProvider[<name>]`, so you can raise the level for a single
provider without the noise from the rest.

Log messages identify the failed validation check, for example:
`JWT algorithm HS256 is not accepted` or `JWT kid abc123 does not match any JWK`.

### Customize the 401 response {id="errors-custom"}

The `bearer {}` configuration does not provide an authentication failure handler. To customize the response, set the
`onUnauthorized` handler on the protected route:

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
configured [protected resource metadata](#protected-resource), the `resource_metadata` parameter is also removed. Add the header
explicitly if clients depend on it.

For routes that accept multiple authentication schemes, use `authenticateWithAnyOf(..., onUnauthorized = ...)`.

### Do not reuse your client ID as the audience {id="audience-overlap"}

If `bearer { audience }` contains the `clientId` from your `oauth { }` block, an ID token issued for login can pass as
an access token for your API, unless the provider marks it with a `token_use` or `typ` claim. Not all providers do.

Assign your API its own resource identifier:

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

The plugin logs a warning at startup when it detects an overlap between the API audience and the OAuth client ID.

> To learn about discovery, token validation settings, and testing, see [](server-oidc.md).
> 
> For browser sign-in with the same provider, see [](server-oidc-browser-login.md).
> 
{style="tip"}

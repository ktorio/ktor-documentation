[//]: # (title: OpenID Connect)

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
The OpenID Connect plugin allows you to configure token validation and browser login from an issuer URL, using the 
provider'sdiscovery document.
</link-summary>

[OpenID Connect](https://openid.net/developers/how-connect-works/) (OIDC) is an identity layer on top of OAuth 2.0.
While OAuth 2.0 provides a framework for delegated authorization, OIDC adds authentication by allowing clients to verify
the identity of an end user. It provides identity information in an ID token.

The `Oidc` plugin supports the following typical scenarios:

* **Protect an API.** Validate tokens issued by an OpenID Connect provider before allowing access to protected routes. For
  more information, see [](server-oidc-resource-server.md).
* **Sign users in.** Redirect users to an OpenID Connect provider for authentication and handle the callback after
  sign-in. For more information, see [](server-oidc-browser-login.md).

Both scenarios start with the provider's issuer URL. The plugin reads the provider's discovery document and works out the
endpoints, signing keys, and supported algorithms.

<include from="lib.topic" element-id="oidc_experimental"/>

## Add dependencies {id="add_dependencies"}

To use the `Oidc` plugin, add the `%artifact_name%` artifact to your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

> This artifact is available for the JVM only.
> 
{style="note"}

## Register an identity provider {id="register"}

Install the `Oidc` plugin and register an identity provider for each issuer:

```kotlin
suspend fun Application.module() {
    val oidc = install(Oidc)

    val google = oidc.identityProvider("google") {
        issuer = "https://accounts.google.com"
        bearer {
            audience = setOf("my-api")
        }
    }
}
```

The `identityProvider()` function retrieves the discovery document before returning, so it
has to be called from a suspending function, such as a [suspend application module](server-modules.md#concurrent-modules).

The provider name is used in generated route paths and authentication scheme names. It must contain lowercase letters,
digits, and hyphen-separated segments. For example, `google` and `my-idp` are valid names, while `Google` and `my_idp`
are not.

Each provider name and issuer must be unique. Registering the same name or issuer more than once throws an
`IllegalArgumentException`.

The `identityProvider()` function returns an `OidcProvider`, which you use to protect routes with. Depending on its
configuration, the provider exposes up to three authentication schemes to use with `authenticateWith()`:

| Configured with                | Scheme                         | Protects                              |
|--------------------------------|--------------------------------|---------------------------------------|
| `bearer { }`                   | `provider.jwtBearer`           | An API receiving JWT access tokens    |
| `bearer { introspection { } }` | `provider.introspectionBearer` | An API receiving opaque access tokens |
| `oauth { }`                    | `provider.session`             | Routes behind a browser login         |

Reading a scheme that has not been configured throws an `IllegalStateException`. The exception message identifies the
configuration block required to enable the scheme. 

> For more details, see [](server-oidc-resource-server.md) and [](server-oidc-browser-login.md).
> 
{style="tip"}

## How discovery works {id="discovery"}

The plugin retrieves `<issuer>/.well-known/openid-configuration`, then reads the provider endpoints and signing keys from
it. You do not need to configure the authorization endpoint, token endpoint, or JWKS URL manually.

The `issuer` value in the discovery document must exactly match your configured `issuer`, including any trailing slash. This
comparison is a security check, so the plugin does not normalize either value.

After the initial request, the plugin re-reads the document at the interval specified by `discoveryRefreshInterval`, which
defaults to 15 minutes. This allows key rotation to reach your application without a restart:

```kotlin
val oidc = install(Oidc) {
    discoveryRefreshInterval = 15.minutes
    initialDiscoveryAttempts = 3
    initialDiscoveryRetryDelay = 5.seconds
}
```

To disable periodic discovery refresh, set `discoveryRefreshInterval` to `Duration.ZERO`.

## Handle discovery failures {id="discovery-errors"}

Discovery failures are handled differently [during application startup](#discovery-errors-startup) and [while the application is running](#discovery-errors-runtime).

### At startup {id="discovery-errors-startup"}

If the initial discovery request fails, `identityProvider()` throws `OidcDiscoveryException`. The exception leaves your
module function, and the application does not start.

By default, the plugin makes one discovery attempt. If your provider may be temporarily unavailable during startup, increase
the number of attempts:

```kotlin
val oidc = install(Oidc) {
    initialDiscoveryAttempts = 5
    initialDiscoveryRetryDelay = 3.seconds
}
```

Retries apply to network and HTTP errors. They do not apply to configuration errors, such as an issuer mismatch, or a 
discovery document that is missing a required endpoint. These errors fail immediately with an `IllegalArgumentException`.

### While running {id="discovery-errors-runtime"}

If a periodic refresh fails, the plugin continues to use the most recently retrieved discovery document. It retries
after `discoveryRefreshFailureDelay`, which defaults to one minute, and continues retrying until it succeeds.

Refresh failures are not logged by default. To monitor them, subscribe to the `OidcMetadataRefreshFailed` event:

```kotlin
monitor.subscribe(OidcMetadataRefreshFailed) { failure ->
    log.warn(
        "OIDC refresh failed for {} ({} in a row)",
        failure.provider.name,
        failure.consecutiveFailures,
        failure.cause
    )
}
```

## Configure static metadata {id="static-metadata"}

If the provider endpoints are known in advance, or you need a static configuration for testing, use the `metadata`
property:

```kotlin
val provider = oidc.identityProvider("static") {
    issuer = issuerUrl
    metadata = OpenIdProviderMetadata(
        issuer = issuerUrl,
        authorizationEndpoint = "$issuerUrl/authorize",
        tokenEndpoint = "$issuerUrl/token",
        jwksUri = "$issuerUrl/jwks",
    )
}
```

Setting `metadata` skips the initial discovery request and disables periodic discovery refresh for that provider. Your
application is then responsible for keeping the metadata current, including after key rotation.

The issuer in the static document must still match the configured `issuer`. The JWKS endpoint is still accessed over
HTTP. To avoid that request during tests, see [](#testing).

## Token types {id="tokens"}

Each authentication [scheme](#register) produces a specific principal type, so a route always knows what it is holding:

| Scheme                         | Principal                | Comes from                              |
|--------------------------------|--------------------------|-----------------------------------------|
| `provider.session`             | `OidcToken.Id`           | A browser login                         |
| `provider.jwtBearer`           | `OidcToken.Access`       | A JWT access token verified locally     |
| `provider.introspectionBearer` | `OidcToken.Introspected` | An access token checked by the provider |

`OidcToken.Id` and `OidcToken.Access` expose `claims` for the raw JWT claims and `userInfo` for the normalized user
fields such as `subject`, `name`, and `email`. `OidcToken.Introspected` exposes `introspection` instead.

## Map a token to an application principal {id="map-principal"}

Routes often work with an application-specific principal rather than an OIDC token. Use the `.mapPrincipal()` function to
create a new authentication scheme that produces your application type:

```kotlin
data class AppUser(val id: String, val email: String?)

val apiAuth = google.jwtBearer.mapPrincipal { token ->
    val id = token.claims.subject ?: return@mapPrincipal null
    AppUser(id, token.userInfo?.email)
}

routing {
    authenticateWith(apiAuth) {
        get("/me") {
            call.respond(call.principal.id)
        }
    }
}
```

Returning `null` rejects the request. You can use this behavior to reject a valid token when the corresponding
application account no longer exists.

Principle mapping runs when the scheme authenticates a route, not during the OAuth callback. For example, if a signed-in
user is later removed from your database, the user is rejected on the next request instead of retaining a valid
application session.

## Configure from a configuration file {id="config-file"}

Store client secrets in a configuration file instead of source code. For example, in your
<path>application.yaml</path> file:

```yaml
ktor:
  oidc:
    google:
      issuer: "https://accounts.google.com"
      clientId: "$GOOGLE_CLIENT_ID"
      clientSecret: "$GOOGLE_CLIENT_SECRET"
      scopes: ["openid", "profile", "email"]
```

`$GOOGLE_CLIENT_ID` and `$GOOGLE_CLIENT_SECRET` reference environment variables.

> For more information on working with configuration files, see [](server-configuration-file.topic).
> 
{style="tip"}

You can then read the configuration as `OidcEnvConfig`:

```kotlin
val env = environment.config
    .property("ktor.oidc.google")
    .getAs<OidcEnvConfig>()

val google = oidc.identityProvider("google") {
    issuer = env.issuer
    oauth {
        clientId = env.clientId
        clientSecret = env.clientSecret
        scopes = env.scopes
    }
}
```

The plugin does not load this configuration automatically. `OidcEnvConfig` is a convenience type for reading the values.
Your application determines where the values come from and how they are applied.

## Configure token validation {id="jwt-config"}

Use the `jwt {}` block to configure how tokens are verified. The defaults are secure, so change them only when required:

```kotlin
oidc.identityProvider("google") {
    issuer = "https://accounts.google.com"
    jwt {
        clockSkew = 30.seconds
        allowedAlgorithms = setOf(
            SignatureAlgorithm.RSA_SHA_256
        )
        jwkCache(maxEntries = 10, duration = 1.hours)
        jwkRateLimit(bucketSize = 10)
    }
}
```

* `clockSkew` specifies the leeway applied to `exp` and `nbf`. It defaults to 60 seconds.
* `allowedAlgorithms` restricts which signature algorithms are accepted. When this option is not set, ID tokens fall back to the
  algorithms the discovery document advertises. Only RSA and EC algorithms can be specified.
* `jwkCache` and `jwkRateLimit` control how often the JWKS endpoint is queried. Rate limiting is enabled by default at
  10 requests per minute. If the limit is exceeded, the request fails with an `OidcSigningKeyUnavailableException` rather than
  rejecting the token. Configure the limit to accommodate expected peak cache-miss traffic. For more information, see
  [](server-oidc-resource-server.md#errors-500).

The `none` algorithm and all HMAC algorithms (`HS256`, `HS384`, `HS512`) are always rejected, regardless of the configuration.
A shared secret is not a safe way to verify a token issued by a third party.

`jwkProviderFactory` cannot be combined with `jwkCache` or `jwkRateLimit`. If you provide a custom JWK provider factory,
your application is responsible for caching.

## Test without an external provider {id="testing"}

`OpenIdTestKeys` generates an in-memory key pair and issues tokens signed with that key. Combined with static metadata, this
gives you a complete OIDC setup with no network calls, while still running the real issuer, audience, algorithm, and
signature checks:

```kotlin
@Test
fun `rejects a token for another audience`() = testApplication {
    val issuerUrl = "https://test-issuer"
    val keys = OpenIdTestKeys.rsa(
        issuer = issuerUrl,
        audience = "my-api"
    )

    application {
        val oidc = install(Oidc)
        val provider = oidc.identityProvider("test") {
            issuer = issuerUrl
            metadata = OpenIdProviderMetadata(
                issuer = issuerUrl,
                authorizationEndpoint = "$issuerUrl/authorize",
                tokenEndpoint = "$issuerUrl/token",
                jwksUri = "$issuerUrl/jwks",
            )
            jwt(keys)
            bearer { audience = setOf("my-api") }
        }

        routing {
            authenticateWith(provider.jwtBearer) {
                get("/protected") {
                    call.respondText(call.principal.value)
                }
            }
        }
    }

    val token = keys.accessToken {
        subject = "user-1"
        audience = "some-other-api"
    }

    val response = client.get("/protected") {
        bearerAuth(token)
    }

    assertEquals(HttpStatusCode.Unauthorized, response.status)
}
```

The `jwt(keys)` function configures the verifier to use an in-memory public key and restricts the allowed algorithm, so
no JWKS request is made.

Use `keys.accessToken { }` to create access tokens and `keys.idToken(subject) { }` for ID tokens. Both accept
`issuer`, `audience`, `expiresAt`, and custom `claim()` values.

To test EC signatures, use the `OpenIdTestKeys.ec()` function instead of the `rsa()` function.

## Review security settings for production {id="production"}

The plugin applies the following security defaults:

* PKCE is enabled for every login and uses `S256`.
* The authorization state is stored in an AES-256-GCM encrypted cookie with a 10-minute lifetime.
* Session cookies are `HttpOnly` and `SameSite=Lax`, and `Secure` outside development mode.
* CSRF protection is enabled for the routes generated by the plugin.
* `nonce` and `at_hash` are checked on every ID token.

Review the following settings before deploying to production:

| Setting                        | Default                      | Why change it                                                                                   |
|--------------------------------|------------------------------|-------------------------------------------------------------------------------------------------|
| `oauth { stateEncryptionKey }` | A new random key per process | In-flight logins break on restart and fail across instances                                     |
| `sessions { storage }`         | `SessionStorageMemory()`     | Sessions are lost on restart and are not shared between instances                               |
| `bearer { audience }`          | —                            | Must not contain your OAuth `clientId`. See [](server-oidc-resource-server.md#audience-overlap) |
| `initialDiscoveryAttempts`     | `1`                          | A single slow response from the provider can prevent your application from starting             |
| `jwt { clockSkew }`            | `60.seconds`                 | Lower the value if your clocks are tightly synchronized                                         |
| `discoveryRefreshInterval`     | `15.minutes`                 | Shorten the interval if your provider rotates keys often                                        |
| `codeChallengeMethod`          | `S256`                       | Keep PKCE enabled                                                                               |
| `sessions { csrfProtection }`  | `originMatchesHost()`        | Keep CSRF protection enabled                                                                    |

The plugin logs a warning at startup for the first three settings when they use these defaults. Consider treating these
warnings as errors in production environments.

## Implemented specifications {id="specs"}

The plugin implements the authorization code flow and the related specifications used by its features:

* [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html) — ID token validation, including
  `nonce`, `azp`, and `at_hash`
* [OpenID Connect Discovery 1.0](https://openid.net/specs/openid-connect-discovery-1_0.html) — [](#discovery)
* [RP-Initiated Logout 1.0](https://openid.net/specs/openid-connect-rpinitiated-1_0.html) —
  [](server-oidc-browser-login.md#logout)
* [RFC 6749](https://www.rfc-editor.org/rfc/rfc6749) §4.1 — the authorization code flow
* [RFC 6750](https://www.rfc-editor.org/rfc/rfc6750) — bearer tokens and `WWW-Authenticate` challenges
* [RFC 7636](https://www.rfc-editor.org/rfc/rfc7636) — [PKCE](server-oidc-browser-login.md#pkce)
* [RFC 7662](https://www.rfc-editor.org/rfc/rfc7662) —
  [token introspection](server-oidc-resource-server.md#introspection)
* [RFC 8707](https://www.rfc-editor.org/rfc/rfc8707) —
  [resource indicators](server-oidc-browser-login.md#resource-indicators)
* [RFC 9207](https://www.rfc-editor.org/rfc/rfc9207) — the `iss` authorization response parameter
* [RFC 9728](https://www.rfc-editor.org/rfc/rfc9728) —
  [protected resource metadata](server-oidc-resource-server.md#protected-resource)

## Limitations {id="limitations"}

* The plugin is available for the JVM only.
* The API is experimental and may change.
* Only the authorization code flow is supported. The implicit and hybrid flows are not.
* PKCE is fixed to `S256`. Custom challenge methods are rejected.
* Encrypted (JWE) UserInfo responses are not supported.
* A login callback that returns no ID token is not supported. For access-token-only login against a provider that does
  not implement OIDC, use the [`oauth`](server-oauth.md) provider.
* The introspection endpoint is not read from discovery. You must configure it explicitly.
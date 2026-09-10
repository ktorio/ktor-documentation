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
The OpenID Connect plugin configures token validation and browser login from an issuer URL, using the provider's
discovery document.
</link-summary>

## What OpenID Connect is {id="what"}

[OpenID Connect](https://openid.net/developers/how-connect-works/) (OIDC) is an identity layer on top of OAuth 2.0.
OAuth 2.0 answers "what is this caller allowed to do". OpenID Connect adds "who is this caller", in the form of a signed
**ID token**.

The Ktor plugin covers the two things applications usually need:

* **Protect an API.** Someone else issued a token, and your server has to check it. See
  [](server-oidc-resource-server.md).
* **Sign users in.** Your server sends the user to the provider and gets them back signed in. See
  [](server-oidc-browser-login.md).

Both start from one thing: the issuer URL. The plugin reads the provider's discovery document and works out the
endpoints, signing keys, and supported algorithms for you.

<include from="lib.topic" element-id="oidc_experimental"/>

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact"/>

This artifact is available for the JVM only.

## Register an identity provider {id="register"}

Install the plugin, then register one provider per issuer:

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

Note that the module function is `suspend`. `identityProvider()` fetches the discovery document before it returns, so it
has to be called from a suspending function. See [](server-modules.md#concurrent-modules).

The provider name appears in generated route paths and scheme names, so it has to be URL-friendly: lowercase letters,
digits, and hyphen-separated segments. `google` and `my-idp` work; `Google` and `my_idp` do not.

Each name and each issuer can be registered only once. Registering the same issuer twice fails with
`IllegalArgumentException`.

`identityProvider()` returns an `OidcProvider`, and that value is how you protect routes. Depending on what you
configured, it exposes up to three **authentication schemes** — the values you pass to `authenticateWith()`:

| Configured with                | Scheme                         | Protects                              |
|--------------------------------|--------------------------------|---------------------------------------|
| `bearer { }`                   | `provider.jwtBearer`           | An API receiving JWT access tokens    |
| `bearer { introspection { } }` | `provider.introspectionBearer` | An API receiving opaque access tokens |
| `oauth { }`                    | `provider.session`             | Routes behind a browser login         |

Reading a scheme you did not configure throws `IllegalStateException`, with a message naming the block to add. The
sibling topics cover each one in full: [](server-oidc-resource-server.md) and [](server-oidc-browser-login.md).

## How discovery works {id="discovery"}

The plugin fetches `<issuer>/.well-known/openid-configuration`, then reads the endpoints and signing keys from it. You
don't need to configure an authorization endpoint, token endpoint, or JWKS URL by hand.

The `issuer` value in that document must match your configured `issuer` **exactly**, including any trailing slash. This
is a security check, so the plugin does not normalize either side.

After the first fetch, the plugin re-reads the document every `discoveryRefreshInterval`, which defaults to 15 minutes.
This is how key rotation reaches your application without a restart.

```kotlin
val oidc = install(Oidc) {
    discoveryRefreshInterval = 15.minutes
    initialDiscoveryAttempts = 3
    initialDiscoveryRetryDelay = 5.seconds
}
```

Set `discoveryRefreshInterval` to `Duration.ZERO` to turn periodic refresh off.

## Handle discovery failures {id="discovery-errors"}

Discovery can fail at two very different moments, and they need different handling.

### At startup {id="discovery-errors-startup"}

If the first fetch fails, `identityProvider()` throws `OidcDiscoveryException`. The exception leaves your module
function, so **the application does not start**.

By default, the plugin tries once. If your provider is sometimes slow to answer when both start at the same time, raise
the attempt count:

```kotlin
val oidc = install(Oidc) {
    initialDiscoveryAttempts = 5
    initialDiscoveryRetryDelay = 3.seconds
}
```

Retries cover network and HTTP errors. They do **not** cover a mismatched issuer or a document that is missing a
required endpoint: those are configuration mistakes, so they fail immediately with `IllegalArgumentException` and
retrying would not help.

### While running {id="discovery-errors-runtime"}

A failed refresh does not affect requests. The plugin keeps the last document it successfully fetched and retries every
`discoveryRefreshFailureDelay`, which defaults to one minute. It never gives up.

There is a catch worth knowing about: **a failed refresh writes nothing to the log**. Unless you subscribe to the event,
a provider that has been unreachable for hours looks exactly like one that is healthy.

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

## Skip discovery with static metadata {id="static-metadata"}

If you already know the endpoints, or you are writing a test, set the metadata yourself:

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

Setting `metadata` skips the startup fetch **and turns off periodic refresh** for that provider. You take over
responsibility for keeping the values current, including after a key rotation.

The issuer in the static document still has to match the configured `issuer`. The JWKS endpoint is still fetched over
HTTP; to avoid that too, see [](#testing).

## Token types {id="tokens"}

Each of those [schemes](#register) produces its own principal type, so a route always knows what it is holding:

| Scheme                         | Principal                | Comes from                              |
|--------------------------------|--------------------------|-----------------------------------------|
| `provider.session`             | `OidcToken.Id`           | A browser login                         |
| `provider.jwtBearer`           | `OidcToken.Access`       | A JWT access token verified locally     |
| `provider.introspectionBearer` | `OidcToken.Introspected` | An access token checked by the provider |

`OidcToken.Id` and `OidcToken.Access` both expose `claims` for the raw JWT claims and `userInfo` for the normalized user
fields (`subject`, `name`, `email`, and so on). `OidcToken.Introspected` exposes `introspection` instead.

## Map a token to your own principal {id="map-principal"}

Routes usually want your own user type rather than a token. `mapPrincipal` returns a new scheme that produces your
type, so name the result after the scheme rather than after a user:

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

Returning `null` rejects the request, so this is also the place to reject a valid token for an account you no longer
recognize.

Mapping runs when a scheme authenticates a route, not during the OAuth callback. A user who is signed in but has since
been deleted from your database is rejected on their next request, not left with a working session.

## Configure from a configuration file {id="config-file"}

Client secrets belong in configuration, not in code. Put the values in `application.yaml`:

```yaml
ktor:
  oidc:
    google:
      issuer: "https://accounts.google.com"
      clientId: "$GOOGLE_CLIENT_ID"
      clientSecret: "$GOOGLE_CLIENT_SECRET"
      scopes: ["openid", "profile", "email"]
```

`$GOOGLE_CLIENT_ID` reads an environment variable. See [](server-configuration-file.topic).

Then read them with `OidcEnvConfig`:

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

The plugin does not read this configuration on its own. `OidcEnvConfig` is a convenience type; you decide where the
values come from and how they are applied.

## Tune token validation {id="jwt-config"}

The `jwt { }` block controls how tokens are verified. The defaults are safe, so change them only when you have a reason:

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

* `clockSkew` is the leeway applied to `exp` and `nbf`. It defaults to 60 seconds.
* `allowedAlgorithms` restricts which signature algorithms are accepted. When it is unset, ID tokens fall back to the
  algorithms the discovery document advertises. Only RSA and EC algorithms can be listed here.
* `jwkCache` and `jwkRateLimit` control how often the JWKS endpoint is queried. Rate limiting is on by default at
  10 requests per minute. Exhausting it fails the request with `OidcSigningKeyUnavailableException` rather than
  rejecting the token, so size it for your peak cache-miss traffic. See
  [](server-oidc-resource-server.md#errors-500).

The `none` algorithm and all HMAC algorithms (`HS256`, `HS384`, `HS512`) are always rejected, whatever you configure. A
shared secret is not a safe way to verify a token that a third party issued.

`jwkProviderFactory` cannot be combined with `jwkCache` or `jwkRateLimit`. If you supply your own factory, caching is
your responsibility.

## Test without a real provider {id="testing"}

`OpenIdTestKeys` generates a key pair in memory and issues tokens signed with it. Combined with static metadata, this
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

`jwt(keys)` points the verifier at the in-memory public key and pins the allowed algorithm, so no JWKS request is made
either. Use `keys.accessToken { }` for access tokens and `keys.idToken(subject) { }` for ID tokens; both accept
`issuer`, `audience`, `expiresAt`, and custom `claim()` values.

Use `OpenIdTestKeys.ec()` instead of `rsa()` to test EC signatures.

## Security defaults and production checklist {id="production"}

The plugin sets these for you. Do not weaken them without a specific reason:

* PKCE is on for every login, using `S256`.
* The authorization state is kept in an AES-256-GCM encrypted cookie with a 10-minute lifetime.
* Session cookies are `HttpOnly` and `SameSite=Lax`, and `Secure` outside development mode.
* CSRF protection is on for the routes the plugin generates.
* `nonce` and `at_hash` are checked on every ID token.

These are the settings worth reviewing before you deploy:

| Setting                        | Default                      | Why change it                                                                                   |
|--------------------------------|------------------------------|-------------------------------------------------------------------------------------------------|
| `oauth { stateEncryptionKey }` | A new random key per process | In-flight logins break on restart and fail across instances                                     |
| `sessions { storage }`         | `SessionStorageMemory()`     | Sessions are lost on restart and are not shared between instances                               |
| `bearer { audience }`          | —                            | Must not contain your OAuth `clientId`. See [](server-oidc-resource-server.md#audience-overlap) |
| `initialDiscoveryAttempts`     | `1`                          | A single slow response from the provider stops your application from starting                   |
| `jwt { clockSkew }`            | `60.seconds`                 | Lower it if your clocks are tightly synchronized                                                |
| `discoveryRefreshInterval`     | `15.minutes`                 | Shorten it if your provider rotates keys often                                                  |
| `codeChallengeMethod`          | `S256`                       | Leave PKCE on                                                                                   |
| `sessions { csrfProtection }`  | `originMatchesHost()`        | Leave CSRF protection on                                                                        |

The plugin logs a warning at startup for the first three. Those warnings are worth treating as errors in a production
build.

## Implemented specifications {id="specs"}

The plugin implements the authorization code flow and the pieces built around it. Each is explained where it is
used; this is the summary.

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
* The introspection endpoint is not read from discovery. You have to configure it explicitly.

## What's next {id="next"}

* [](server-oidc-resource-server.md) covers validating tokens in an API.
* [](server-oidc-browser-login.md) covers signing users in from a browser.
* [](server-typed-auth.md) covers the type-safe authentication API these schemes are built on.

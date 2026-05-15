[//]: # (title: What's new in Ktor 3.5.0)

<show-structure for="chapter,procedure" depth="2"/>

_[Released: April 22, 2026](releases.md#release-details)_

## Ktor Server

### RFC 7616 Digest authentication support

Ktor 3.5.0 updates the [`digest` authentication provider](server-digest-auth.md) to comply with [RFC 7616](https://datatracker.ietf.org/doc/html/rfc7616),
improving security and adding support for modern Digest features.

This release introduces the following changes:

* You can now configure multiple hash algorithms using the `algorithms` property. When multiple values are specified, 
  Ktor sends multiple `WWW-Authenticate` headers so clients can choose the strongest supported option.
* Introduced the `DigestAlgorithm` and `DigestQop` enums to replace string-based configuration.
* The `digestProvider {}` lambda now receives an `algorithm` parameter, allowing you to compute the correct digest
  dynamically.
* The `qop` parameter is now included in authentication challenges, in accordance with RFC 7616.
* Added support for session-based algorithms such as `SHA-256-sess` and `SHA-512-256-sess`.
* Added support for RFC 7616 username hashing (`userhash`) for improved privacy protection.

The following example shows how to migrate from the legacy configuration to the RFC 7616–compliant API:

<compare type="left-right" first-title="Legacy" second-title="RFC 7616">

```kotlin
install(Authentication) {
    digest("auth") {
        realm = "MyRealm"
        algorithmName = "MD5"  // Deprecated property
        digestProvider { userName, realm ->
            // Old signature without algorithm parameter
            getMd5Digest("$userName:$realm:$password")
        }
    }
}
```

```kotlin
install(Authentication) {
    digest("auth") {
        realm = "MyRealm"
        // Support both modern and legacy clients
        algorithms = listOf(DigestAlgorithm.SHA_512_256, DigestAlgorithm.MD5)
        digestProvider { userName, realm, algorithm ->
            // New signature receives the algorithm
            val password = getPassword(userName) ?: return@digestProvider null
            algorithm.toDigester().digest("$userName:$realm:$password".toByteArray())
        }
    }
}
```
</compare>

Existing configurations continue to work without changes. However, for new applications, it is recommended to:

* Use secure algorithms such as `SHA-512-256` or `SHA-256`.
* Update `digestProvider` to use the new `algorithms` parameter.
* Avoid `MD5`-based algorithms unless required for legacy client compatibility.

For a complete guide, see [](server-digest-auth.md).

### Suspending `.authenticate()` overload in custom providers

[Custom authentication providers](server-auth.md#custom-auth-provider) can now implement a suspending version
of the `DynamicProviderConfig.authenticate()` function. The `.authenticate()` function accepts a suspending lambda, so
you can call coroutine APIs directly inside authentication:

```kotlin
install(Authentication) {
  provider("custom") {
    authenticate { context ->
      delay(10.milliseconds)
      context.principal(null)
    }
  }
}
```

### Root configuration data class mapping

`ApplicationConfig` now provides a `.getAs()` function for deserializing the entire configuration into a data class.

Previously, deserialization was limited to individual properties, requiring access through the `.property()` function.
With root-level support, you can map the full configuration structure directly to a single data class:

<compare type="top-bottom" first-title="Before" second-title="After">

```kotlin
@Serializable data class App(val port: Int, val host: String)
@Serializable data class Security(val clientId: String, val clientSecret: String)


val app = ApplicationConfig("application.yaml").property("app").getAs<App>()
val security = ApplicationConfig("application.yaml").property("security").getAs<Security>()
```

```kotlin
@Serializable data class App(val port: Int, val host: String)
@Serializable data class Security(val clientId: String, val clientSecret: String)
@Serializable data class Config(val app: App, val security: Security)

val config = ApplicationConfig("application.yaml").getAs<Config>()
```

</compare>

### Require request parameters helper functions

Ktor 3.5.0 introduces a new set of extension functions that simplify accessing required request data from
an `ApplicationCall`.

Previously, validating required request data often required repetitive null checks and labeled returns.
To improve this workflow, Ktor now provides the following new extension functions:

* `ApplicationCall.requireQueryParameter()` — retrieves a required query parameter from the request URL. Throws if the
  parameter is missing.
* `ApplicationCall.requireHeader()` — retrieves a required HTTP header value. Throws if the header is not present in
  the request.
* `ApplicationCall.requireCookie()` — retrieves a required cookie value, optionally decoding it using the specified
  encoding. Throws if the cookie is missing.
* `RoutingCall.requirePathParameter()` — retrieves a required path parameter from the route definition. Throws if
  the parameter is not present in the matched route.

Each function returns a non-null value or throws `MissingRequestParameterException` if the value is missing.

<compare>

```kotlin
post("/checkout") {
  val userId = call.request.cookies["userId"]
    ?: return@post call.respondText(
      "Login required",
      status = HttpStatusCode.Forbidden
    )

  val amount = call.request.queryParameters["amount"]?.toLongOrNull()
    ?: return@post call.respondText(
     "Amount missing",
     status = HttpStatusCode.BadRequest
  )
  
  // Business logic
}
```

```kotlin
post("/checkout") {
    val userId = call.requireCookie("userId")
    val amount = call.requireQueryParameter("amount").toLong()

    // Business logic
}
```

</compare>

### ES modules compatibility for `ktor-network`

We’ve fixed issues that made it impossible to use `ktor-network` and all dependent modules when ES modules were enabled.

To help prevent future regressions, our JavaScript test infrastructure now targets both ES2015 and ES modules by default.

> For more information about Kotlin/JS module systems and ES2015 support, see:
> * [JavaScript modules](https://kotlinlang.org/docs/js-modules.html)
> * [Support for ES2015 features](https://kotlinlang.org/docs/js-project-setup.html#support-for-es2015-features)
>
{style="tip"}

### Send session cookies only when modified

Ktor 3.5.0 introduces a new option for the [Sessions](server-sessions.md) plugin that sends session data only
when it changes (for example, the `Set-Cookie` header for cookie-based sessions).

By default, session data is sent on every response to preserve existing behavior. To send it only when modified, enable
the `sendOnlyIfModified` flag in the session cookie configuration:

```kotlin
install(Sessions) {
    cookie<MySession>("SESSION") {
        sendOnlyIfModified = true
    }
}
```

## Ktor Client

### Custom DNS resolvers in the OkHttp and Apache5 engines

Ktor 3.5.0 adds first-class support for configuring custom DNS resolvers in the OkHttp and Apache5 client engines.

Previously, you configured custom DNS resolution by accessing engine-specific internals, such as `config {}` in OkHttp 
or `configureConnectionManager { setDnsResolver(...) }` in Apache5. Ktor now exposes dedicated configuration properties
on each engine to provide a consistent and type-safe API.

#### OkHttp

You can now configure a custom DNS resolver in OkHttp using the `OkHttpConfig.dns` property:

```kotlin
HttpClient(OkHttp) {
    engine {
        dns = Dns { hostname -> listOf(InetAddress.getByName("127.0.0.1")) }
    }
}
```

If you do not configure the `dns` property, the OkHttp engine continues to use OkHttp’s default `Dns.SYSTEM` resolver.

#### Apache5

You can now configure a custom DNS resolver in Apache5 using the `Apache5EngineConfig.dnsResolver` property:

```kotlin
HttpClient(Apache5) {
    engine {
        dnsResolver = SystemDefaultDnsResolver.INSTANCE
    }
}
```

If the `dnsResolver` property is not configured, the Apache5 engine continues to use the Apache client’s default DNS
resolver.
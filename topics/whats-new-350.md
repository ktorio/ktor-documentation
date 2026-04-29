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

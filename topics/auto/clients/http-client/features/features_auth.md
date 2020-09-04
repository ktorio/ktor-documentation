[//]: # (title: Auth)
[//]: # (category: clients)
[//]: # (caption: Auth)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.client.features.auth.Auth)
[//]: # (keywords: authentication)
[//]: # (redirect_from: redirect_from)
[//]: # (- /clients/http-client/features/basic-auth.html: - /clients/http-client/features/basic-auth.html)
[//]: # (ktor_version_review: 1.2.0)

Ktor client supports authentication out of the box as a standard pluggable feature.

## Installation

``` kotlin
val client = HttpClient() {
    install(Auth) {
        // providers config
        ...
    }
}
```

## Providers

### Basic

This provider sends an `Authorization: Basic` with the specified credentials:

```kotlin
val client = HttpClient() {
    install(Auth) {
        basic {
            username = "username"
            password = "password"
        }
    }
}
```

This feature implements the IETF's [RFC 7617](https://tools.ietf.org/html/rfc7617).

### Digest

This provider sends an `Authorization: Digest` with the specified credentials:

```kotlin
val client = HttpClient() {
    install(Auth) {
        digest {
            username = "username"
            password = "password"
            realm = "custom"
        }
    }
}
```

This feature implements the IETF's [RFC 2617](https://tools.ietf.org/html/rfc2617).
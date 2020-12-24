[//]: # (title: Auth)

<include src="lib.md" include-id="outdated_warning"/>

Ktor client supports authentication out of the box as a standard pluggable feature.

## Add Dependencies {id="add_dependencies"}
To enable authentication, you need to include the following artifacts in the build script:
* JVM:

   <var name="artifact_name" value="ktor-client-auth-jvm"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>

* JS:

   <var name="artifact_name" value="ktor-client-auth-js"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>

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
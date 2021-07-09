[//]: # (title: Auth)

<include src="lib.xml" include-id="outdated_warning"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-client-auth</code>
</p>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-basic">client-auth-basic</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-digest">client-auth-digest</a>
</p>
</microformat>

Ktor client supports authentication out of the box as a plugin (previously known as feature).

## Add dependencies {id="add_dependencies"}

To enable authentication, you need to include the `ktor-client-auth` artifact in the build script:

<var name="artifact_name" value="ktor-client-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Installation

```kotlin
val client = HttpClient() {
    install(Auth) {
        // Provider config
    }
}
```

## Providers

### Basic

This provider sends an `Authorization: Basic` with the specified credentials:

```kotlin
```
{src="snippets/client-auth-basic/src/main/kotlin/com/example/Application.kt" lines="13-21"}

You can find the full example here: [client-auth-basic](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-basic).

> This provider implements the IETF's [RFC 7617](https://tools.ietf.org/html/rfc7617).

### Digest

This provider sends an `Authorization: Digest` with the specified credentials:

```kotlin
val client = HttpClient() {
    install(Auth) {
        digest {
            credentials {
                DigestAuthCredentials(username = "Hello", password = "World!")
            }

            realm = "custom"
        }
    }
}
```
{src="snippets/client-auth-digest/src/main/kotlin/com/example/Application.kt" lines="13-22"}

You can find the full example here: [client-auth-digest](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-digest).

> This provider implements the IETF's [RFC 2617](https://tools.ietf.org/html/rfc2617).

### Bearer

This provider sends `Authorization: Bearer` with the provided token. To configure `Bearer` authentication, you need to
define how to get an initial token (using `loadTokens`) and how to obtain a new token if the old one is invalid (using `refreshTokens`):

```kotlin
val client = HttpClient() {
    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(accessToken = "hello", refreshToken = "world")
            }

            refreshTokens { response: HttpResponse ->
                BearerTokens(accessToken = "hello", refreshToken = "world")
            }
        }
    }
}
```

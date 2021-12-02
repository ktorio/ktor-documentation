[//]: # (title: Basic authentication)

<microformat>
<var name="example_name" value="client-auth-basic"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The Basic [authentication scheme](auth.md) can be used for logging in users. In this scheme, user credentials are transmitted as username/password pairs encoded using Base64. You can learn how the basic authentication flow might look from the [Basic authentication flow](basic.md#flow) section for a Ktor server.

To send user credentials in the `Authorization` header using the `Basic` scheme, you need to configure the `basic` authentication provider as follows:

1. Call the [basic](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/basic.html) function inside the `install` block.
2. Provide the required credentials using [BasicAuthCredentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-basic-auth-credentials/index.html) and pass this object to the [credentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-basic-auth-config/credentials.html) function.
3. Optionally, configure the realm using the `realm` property.

```kotlin
```
{src="snippets/client-auth-basic/src/main/kotlin/com/example/Application.kt" lines="13-22"}

You can find the full example here: [client-auth-basic](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-basic).

The client also allows you to send credentials in the initial request without waiting for a `401` (Unauthorized) response with the `WWW-Authenticate` header. You need to call the `sendWithoutRequest` function returning boolean and check the request parameters.

```kotlin
install(Auth) {
    basic {
        // ...
        sendWithoutRequest { request ->
            request.url.host == "0.0.0.0"
        }
    }
}
```

[//]: # (title: Digest authentication)

<microformat>
<var name="example_name" value="client-auth-digest"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

In the Digest authentication scheme, a hash function is applied to a username and password before sending them over the network. You can learn how the digest authentication flow might look from the [Digest authentication flow](digest.md#flow) section for a Ktor server.

To send user credentials in the `Authorization` header using the `Digest` scheme, you need to configure the `digest` authentication provider as follows:

1. Call the [digest](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/digest.html) function inside the `install` block.
2. Provide the required credentials using [DigestAuthCredentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-digest-auth-credentials/index.html) and pass this object to the [credentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-digest-auth-config/credentials.html) function.
3. Optionally, configure the realm using the `realm` property.


```kotlin
```
{src="snippets/client-auth-digest/src/main/kotlin/com/example/Application.kt" lines="13-22"}

You can find the full example here: [client-auth-digest](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-digest).
[//]: # (title: Digest authentication)

<microformat>
<var name="example_name" value="client-auth-digest"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

In the Digest authentication scheme, a hash function is applied to a username and password before sending them over the network. 

## Digest authentication flow {id="flow"}

The digest authentication flow looks as follows:

1. A client makes a request without the `Authorization` header to a specific resource in a server application.
2. A server responds to a client with a `401` (Unauthorized) response status and uses a `WWW-Authenticate` response header to provide information that the digest authentication scheme is used to protect a route. A typical `WWW-Authenticate` header looks like this:

   ```
   WWW-Authenticate: Digest
           realm="Access to the '/' path",
           nonce="e4549c0548886bc2",
           algorithm="MD5"
   ```
   {style="block"}

3. Usually a client displays a login dialog where a user can enter credentials. Then, a client makes a request with the following `Authorization` header:

   ```
   Authorization: Digest username="jetbrains",
           realm="Access to the '/' path",
           nonce="e4549c0548886bc2",
           uri="/",
           algorithm=MD5,
           response="6299988bb4f05c0d8ad44295873858cf"
   ```
   {style="block"}

   The `response` value is generated in the following way:

   a. `HA1 = MD5(username:realm:password)`

   b. `HA2 = MD5(method:digestURI)`

   c. `response = MD5(HA1:nonce:HA2)`

4. A server validates credentials sent by the client and responds with the requested content.


## Configure digest authentication {id="configure"}

To send user credentials in the `Authorization` header using the `Digest` scheme, you need to configure the `digest` authentication provider as follows:

1. Call the [digest](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-auth/io.ktor.client.plugins.auth.providers/digest.html) function inside the `install` block.
2. Provide the required credentials using [DigestAuthCredentials](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-auth/io.ktor.client.plugins.auth.providers/-digest-auth-credentials/index.html) and pass this object to the [credentials](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-auth/io.ktor.client.plugins.auth.providers/-digest-auth-config/credentials.html) function.
3. Optionally, configure the realm using the `realm` property.


```kotlin
```
{src="snippets/client-auth-digest/src/main/kotlin/com/example/Application.kt" lines="13-22"}

> You can find the full example here: [client-auth-digest](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-auth-digest).
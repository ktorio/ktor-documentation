[//]: # (title: Authentication and authorization)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-client-auth</code>
</p>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-basic">client-auth-basic</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-digest">client-auth-digest</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-oauth-google">client-auth-oauth-google</a>
</p>
</microformat>

<excerpt>
The Auth plugin handles authentication and authorization in your client application.
</excerpt>

Ktor provides the `Auth` plugin to handle authentication and authorization in your client application. Typical usage scenarios include logging in users and gaining access to specific resources. 


## Supported authentication types {id="supported"}

HTTP provides a [general framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) for access control and authentication. The Ktor client allows you to use the following HTTP authentication schemes:

* [Basic](#basic) - uses `Base64` encoding to provide a username and password. Generally is not recommended if not used in combination with HTTPS.
* [Digest](#digest) - an authentication method that communicates user credentials in an encrypted form by applying a hash function to the username and password.
* [Bearer](#bearer) - an authentication scheme that involves security tokens called bearer tokens. For example, you can use this scheme as a part of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on.

## Add dependencies {id="add_dependencies"}

To enable authentication, you need to include the `ktor-client-auth` artifact in the build script:

<var name="artifact_name" value="ktor-client-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install Auth {id="install_plugin"}
To install the `Auth` plugin, pass it to the `install` function inside a [client configuration block](client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    install(Auth) {
        // Configure authentication
    }
}
```
Now you can [configure](#configure_authentication) the required authentication provider.



## Configure authentication {id="configure_authentication"}

### Basic {id="basic"}

The Basic authentication scheme can be used for logging in users. In this scheme, user credentials are transmitted as username/password pairs encoded using Base64. You can learn how the basic authentication flow might look from the [Basic authentication flow](basic.md#flow) section for a Ktor server.

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


### Digest {id="digest"}

In the Digest authentication scheme, a hash function is applied to a username and password before sending them over the network. You can learn how the digest authentication flow might look from the [Digest authentication flow](digest.md#flow) section for a Ktor server.

To send user credentials in the `Authorization` header using the `Digest` scheme, you need to configure the `digest` authentication provider as follows:

1. Call the [digest](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/digest.html) function inside the `install` block.
2. Provide the required credentials using [DigestAuthCredentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-digest-auth-credentials/index.html) and pass this object to the [credentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-digest-auth-config/credentials.html) function.
3. Optionally, configure the realm using the `realm` property.


```kotlin
```
{src="snippets/client-auth-digest/src/main/kotlin/com/example/Application.kt" lines="13-22"}

You can find the full example here: [client-auth-digest](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-digest).


### Bearer {id="bearer"}

Bearer authentication involves security tokens called bearer tokens. As an example, these tokens can be used as a part of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on.

A Ktor client allows you to configure a token to be sent in the `Authorization` header using the `Bearer` scheme. You can also specify logic for refreshing a token if the old one is invalid. To configure the `bearer` provider, follow the steps below:

1. Call the [bearer](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/bearer.html) function inside the `install` block.
2. Specify how to get an initial token using `loadTokens`. A code snippet below shows how to [make a POST request](request.md#form_parameters) to get the access and refresh tokens for accessing Google APIs.
   ```kotlin
   ```
   {src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="59-63,76-77"}

3. Specify how to obtain a new token if the old one is invalid using `refreshTokens`. Note that this block will be called after receiving a `401` (Unauthorized) response with the `WWW-Authenticate` header. The example below shows how to get a new access token using a refresh token.
   ```kotlin
   ```
   {src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="59-60,64-77"}

After configuring the `bearer` provider, you can make requests to the protected API.

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="86-93"}

You can find the full example here: [client-auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-oauth-google).

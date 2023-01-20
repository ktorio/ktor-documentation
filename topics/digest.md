[//]: # (title: Digest authentication)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="auth-digest"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

The Digest authentication scheme is a part of the [HTTP framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) used for access control and authentication. In this scheme, a hash function is applied to a username and password before sending them over the network.

Ktor allows you to use digest authentication for logging in users and protecting specific [routes](Routing_in_Ktor.md). You can get general information about authentication in Ktor in the [](authentication.md) section.

## Add dependencies {id="add_dependencies"}
To enable `digest` authentication, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Digest authentication flow {id="flow"}

The digest authentication flow looks as follows:

1. A client makes a request without the `Authorization` header to a specific [route](Routing_in_Ktor.md) in a server application.
1. A server responds to a client with a `401` (Unauthorized) response status and uses a `WWW-Authenticate` response header to provide information that the digest authentication scheme is used to protect a route. A typical `WWW-Authenticate` header looks like this:

   ```
   WWW-Authenticate: Digest
           realm="Access to the '/' path",
           nonce="e4549c0548886bc2",
           algorithm="MD5"
   ```
   {style="block"}

   In Ktor, you can specify the realm and the way of generating a nonce value when [configuring](#configure-provider) the `digest` authentication provider.

1. Usually a client displays a login dialog where a user can enter credentials. Then, a client makes a request with the following `Authorization` header:

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
   > This part [is stored](#digest-table) on a server and can be used by Ktor to validate user credentials.
   
   b. `HA2 = MD5(method:digestURI)`
   
   c. `response = MD5(HA1:nonce:HA2)`

1. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content.


## Install digest authentication {id="install"}
To install the `digest` authentication provider, call the [digest](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/digest.html) function inside the `install` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    digest {
        // Configure digest authentication
    }
}
```
You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure digest authentication {id="configure"}

To get a general idea of how to configure different authentication providers in Ktor, see [](authentication.md#configure). In this section, we'll see on configuration specifics of the `digest` authentication provider.

### Step 1: Provide a user table with digests {id="digest-table"}

The `digest` authentication provider validates user credentials using the `HA1` part of a digest message. So, you can provide a user table that contains usernames and corresponding `HA1` hashes. In the example below, the `getMd5Digest` function is used to generate `HA1` hashes:

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="11-17"}


### Step 2: Configure a digest provider {id="configure-provider"}

The `digest` authentication provider exposes its settings via the [DigestAuthenticationProvider.Config](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-digest-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `realm` property sets the realm to be passed in the `WWW-Authenticate` header.
* The `digestProvider` function fetches the `HA1` part of digest for a specified username.
* (Optional) The `validate` function allows you to map the credentials to a custom principal.

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="18-34,42-44"}

You can also use the [nonceManager](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-digest-authentication-provider/-config/nonce-manager.html) property to specify how to generate nonce values.


### Step 3: Protect specific resources {id="authenticate-route"}

After configuring the `digest` provider, you can protect specific resources in our application using the **[authenticate](authentication.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="35-41"}

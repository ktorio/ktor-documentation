[//]: # (title: Bearer authentication)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="auth-bearer"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The Bearer authentication scheme is a part of the [HTTP framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) used for access control and authentication. This scheme involves security tokens called bearer tokens. The Bearer authentication scheme is used as part of [OAuth](oauth.md) or [JWT](jwt.md), but you can also provide custom logic for authorizing bearer tokens.

You can get general information about authentication in Ktor in the [](authentication.md) section.

> Bearer authentication should only be used over [HTTPS/TLS](ssl.md).

## Add dependencies {id="add_dependencies"}
To enable `bearer` authentication, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Bearer authentication flow {id="flow"}

In general, the Bearer authentication flow might look as follows:

1. After a user successfully authenticates and authorizes access, the server returns an access token to the client.
2. The client can make a request to a protected resource with a token passed in the `Authorization` header using the `Bearer` schema.
   ```HTTP
   ```
   {src="snippets/auth-bearer/get.http"}
3. A server receives a request and [validates](#configure) a token.
4. After validation, a server responds with the contents of a protected resource.




## Install bearer authentication {id="install"}
To install the `bearer` authentication provider, call the [bearer](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/bearer.html) function inside the `install` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    bearer {
        // Configure bearer authentication
    }
}
```

You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure bearer authentication {id="configure"}

To get a general idea of how to configure different authentication providers in Ktor, see [](authentication.md#configure). In this section, we'll see on configuration specifics of the `bearer` authentication provider. 

### Step 1: Configure a bearer provider {id="configure-provider"}

The `bearer` authentication provider exposes its settings via the [BearerAuthenticationProvider.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-bearer-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `realm` property sets the realm to be passed in the `WWW-Authenticate` header.
* The `authenticate` function checks the token sent by the client and returns a `UserIdPrincipal` in the case of successful authentication or `null` if authentication fails.

```kotlin
```
{src="snippets/auth-bearer/src/main/kotlin/com/example/Application.kt" include-lines="9-20"}


### Step 2: Define authorization scope {id="authenticate-route"}

After configuring the `bearer` provider, you can define the authorization for the different resources in our application using the **[authenticate](authentication.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-id-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-bearer/src/main/kotlin/com/example/Application.kt" include-lines="21-27"}

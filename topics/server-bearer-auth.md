[//]: # (title: Bearer authentication in Ktor Server)

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

The Bearer authentication scheme is a part of the [HTTP framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) used for access control and authentication. This scheme involves security tokens called bearer tokens. The Bearer authentication scheme is used as part of [OAuth](server-oauth.md) or [JWT](server-jwt.md), but you can also provide custom logic for authorizing bearer tokens.

You can get general information about authentication in Ktor in the [](server-auth.md) section.

> Bearer authentication should only be used over [HTTPS/TLS](server-ssl.md).

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
To install the `bearer` authentication provider, call the [bearer](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/bearer.html) function inside the `install` block:

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

You can optionally specify a [provider name](server-auth.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure bearer authentication {id="configure"}

To get a general idea of how to configure different authentication providers in Ktor, see [](server-auth.md#configure). In this section, we'll see on configuration specifics of the `bearer` authentication provider. 

### Step 1: Configure a bearer provider {id="configure-provider"}

<tabs group="auth-dsl">
<tab title="Classic" group-key="classic">

The `bearer` authentication provider exposes its settings via the [BearerAuthenticationProvider.Configuration](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-bearer-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `realm` property sets the realm to be passed in the `WWW-Authenticate` header.
* The `authenticate` function checks the token sent by the client and returns a `UserIdPrincipal` in the case of successful authentication or `null` if authentication fails.

```kotlin
```
{src="snippets/auth-bearer/src/main/kotlin/com/example/Application.kt" include-lines="9-20"}

</tab>
<tab title="Type-safe" group-key="typed">

<include from="lib.topic" element-id="typed_auth_experimental"/>

The `bearer()` function creates a scheme for a principal type of your choice. There is no `install(Authentication)`
step: the scheme is a value you pass to the routes that need it.

```kotlin
data class User(val name: String)

val bearerAuth = bearer<User>("auth-bearer") {
    realm = "Access to the '/' path"
    validate { tokenCredential ->
        if (tokenCredential.token == "abc123") {
            User("jetbrains")
        } else {
            null
        }
    }
}
```

Note the name change: the classic provider uses `authenticate`, while the type-safe scheme uses `validate`, like
every other type-safe scheme. It returns your principal type, or `null` if authentication fails.

You can also set `authHeader` to read the token from somewhere other than the `Authorization` header, and
`authSchemes` to accept schemes other than `Bearer`. For the full API, see [](server-typed-auth.md).

</tab>
</tabs>

### Step 2: Protect specific resources {id="authenticate-route"}

<tabs group="auth-dsl">
<tab title="Classic" group-key="classic">

After configuring the `bearer` provider, you can protect specific resources in our application using the **[authenticate](server-auth.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-user-id-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-bearer/src/main/kotlin/com/example/Application.kt" include-lines="21-27"}

</tab>
<tab title="Type-safe" group-key="typed">

Pass the scheme to `authenticateWith()`. Inside the block, `call.principal` is your principal type and is never
`null`, so no cast or null check is needed:

```kotlin
routing {
    authenticateWith(bearerAuth) {
        get("/") {
            call.respondText("Hello, ${call.principal.name}!")
        }
    }
}
```

</tab>
</tabs>

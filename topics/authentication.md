[//]: # (title: Authentication and authorization)

<microformat>
<p>
Code examples: <a href="samples.md" anchor="authentication">Authentication</a>
</p>
</microformat>

Ktor provides the `Authentication` feature to handle authentication and access control. Typical usage scenarios include logging in users, granting access to specific resources, and secure transmitting information between parties. You can also use `Authentication` with [Sessions](sessions.md) to keep a user's information between requests.

## Supported authentication types {id="supported"}
Ktor supports different authentication schemes: from general HTTP authentication schemes such as Basic and Digest to OAuth 2.0 for accessing external resources.

### HTTP authentication {id="http-auth"}
HTTP provides a [general framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) for access control and authentication. In Ktor, you can use the following HTTP authentication schemes:
* [Basic](basic.md) - an authentication method to provide a user name and password encoded using `Base64`.
* [Digest](digest.md) - an authentication method that communicates user credentials in an encrypted form by applying a hash function to the username and password.
* `Bearer` - an authentication scheme that involves security tokens called bearer tokens. You can use [JSON Web Tokens](#jwt) as bearer tokens and use the `jwt` authentication in Ktor to verify a token and validate the claims contained within it.


### Form authentication {id="form-auth"}
[Form](form.md) or form-based authentication uses a [web form](https://developer.mozilla.org/en-US/docs/Learn/Forms) to collect credential information and authenticate a user.


### JSON Web Tokens (JWT) {id="jwt"}
[JSON Web Token](jwt.md) is an open standard for securely transmitting information between parties as a JSON object. You can JSON Web Tokens for authorization: when the user is logged in, each request will include a token, allowing the user to access resources that are permitted with that token. In Ktor, you can verify a token and validate the claims contained within it using the `jwt` authentication.


### LDAP {id="ldap"}
[LDAP](ldap.md) is an open and cross-platform protocol used for directory services authentication. Ktor provides the [ldapAuthenticate](https://api.ktor.io/%ktor_version%/io.ktor.auth.ldap/ldap-authenticate.html) function to authenticate user credentials against a specified LDAP server.

### OAuth {id="oauth"}
[OAuth](oauth.md) is an open standard for securing access to APIs. The `oauth` provider in Ktor allows you to implement authentication using external providers like Google, Facebook, Twitter, and so on.

### Session {id="sessions"}
[Sessions](sessions.md) provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, a user that already has an associated session can be authenticated using the `session` provider. Learn how to do this from [](session-auth.md).


## Add dependencies {id="add_dependencies"}
To enable authentication, you need to include the `ktor-auth` artifact in the build script:

<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

Note that some authentication providers, such as [JWT](jwt.md) and [LDAP](ldap.md), require additional artifacts.



## Install Authentication {id="install"}
<var name="feature_name" value="Authentication"/>
<include src="lib.md" include-id="install_feature"/>


## Configure Authentication {id="configure"}
After [installing Authentication](#install), you can configure and use `Authentication` as follows:


### Step 1: Choose an authentication provider {id="choose-provider"}

To use a specific authentication provider ([basic](basic.md), [digest](digest.md), [form](form.md), and so on), you need to call a corresponding function inside the `install` block. For example, to use the `basic` authentication, call the [basic](https://api.ktor.io/%ktor_version%/io.ktor.auth/basic.html) function:

```kotlin
install(Authentication) {
    basic {
        // [[[Configure basic authentication|basic.md]]]
    }
}
```
Inside this function, you can [configure](#configure-provider) settings specific to this provider.


### Step 2: Specify a provider name {id="provider-name"}

A function for [using a specific provider](#choose-provider) optionally allows you to specify a provider name. A code sample below installs the [basic](https://api.ktor.io/%ktor_version%/io.ktor.auth/basic.html) and [form](https://api.ktor.io/%ktor_version%/io.ktor.auth/form.html) providers with the _auth-basic_ and _auth-form_ names, respectively:

```kotlin
install(Authentication) {
    basic("auth-basic") {
        // [[[Configure basic authentication|basic.md]]]
    }
    form("auth-form") {
        // [[[Configure form authentication|form.md]]]
    }
    // ...
}
```
{disable-links="false"}

These names can be used later to [authenticate different routes](#authenticate-route) using different providers.
> Note that a provider name should be unique, and you can define only one provider without a name.


### Step 3: Configure a provider {id="configure-provider"}

Each [provider type](#choose-provider) has its own configuration. For instance, the [BasicAuthenticationProvider.Configuration](https://api.ktor.io/%ktor_version%/io.ktor.auth/-basic-authentication-provider/-configuration/index.html) class contains options passed to the [basic](https://api.ktor.io/%ktor_version%/io.ktor.auth/basic.html) function. The most important function exposed by this class is [validate](https://api.ktor.io/%ktor_version%/io.ktor.auth/-basic-authentication-provider/-configuration/validate.html) that validates a user name and password. A code sample below shows how it can look:

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="9-20"}

To understand how the `validate` function works, we need to introduce two terms:
* A _principal_ is an entity that can be authenticated: a user, a computer, a service, etc. In Ktor, various authentication providers might use different principals. For example, the `basic` and `form` providers authenticate [UserIdPrincipal](https://api.ktor.io/%ktor_version%/io.ktor.auth/-user-id-principal/index.html) while the `jwt` provider verifies [JWTPrincipal](https://api.ktor.io/%ktor_version%/io.ktor.auth.jwt/-j-w-t-principal/index.html).
  > If you use [session authentication](session-auth.md), a principal might be a data class that stores session data.
* A _credential_ is a set of properties for a server to authenticate a principal: a user/password pair, an API key, and so on. For instance, the `basic` and `form` providers use [UserPasswordCredential](https://api.ktor.io/%ktor_version%/io.ktor.auth/-user-password-credential/index.html) to validate a user name and password while `jwt` validates [JWTCredential](https://api.ktor.io/%ktor_version%/io.ktor.auth.jwt/-j-w-t-credential/index.html).

So, the `validate` function checks a specified [Credential](https://api.ktor.io/%ktor_version%/io.ktor.auth/-credential.html) and returns a [Principal](https://api.ktor.io/%ktor_version%/io.ktor.auth/-principal.html) in a case of successful authentication or `null` if authentication fails.

> To skip authentication based on a specific criteria, use [skipWhen](https://api.ktor.io/%ktor_version%/io.ktor.auth/-authentication-provider/-configuration/skip-when.html). For example, you can skip `basic` authentication if a [session](sessions.md) already exists:
> ```kotlin
> basic {
>     skipWhen { call -> call.sessions.get<UserSession>() != null }
> }


### Step 4: Authenticate routes {id="authenticate-route"}

After defining and configuring an authentication provider, you can authenticate specific [routes](Routing_in_Ktor.md) using the
[authenticate](https://api.ktor.io/%ktor_version%/io.ktor.auth/authenticate.html) function. This function can accept [a name of a provider](#provider-name) used to authenticate nested routes. The code snippet below uses a provider with the _auth-basic_ name to authenticate the `/login` and `/orders` routes:

```kotlin
routing {
    authenticate("auth-basic") {
        get("/login") {
            // ...
        }    
        get("/orders") {
            // ...
        }    
    }
    get("/") {
        // ...
    }
}
```

Note that you can omit a provider name to use an unnamed provider.


### Step 5: Get a principal inside a route handler {id="get-principal"}

In a case of successful authentication, you can retrieve an authenticated [Principal](https://api.ktor.io/%ktor_version%/io.ktor.auth/-principal.html) inside a route handler using the [call.principal](https://api.ktor.io/%ktor_version%/io.ktor.auth/principal.html) function. This function accepts a specific principal type returned by the [configured authentication provider](#configure-provider). In a code sample below, `call.principal` is used to obtain `UserIdPrincipal` and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="21-27"}


If you use [session authentication](session-auth.md), a principal might be a data class that stores session data. So, you need to pass this data class to `call.principal`:

```kotlin
```
{src="snippets/auth-session/src/main/kotlin/com/example/Application.kt" lines="47-53,57-59,64"}





[//]: # (title: Authentication and authorization)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="Authentication"/>
<var name="package_name" value="io.ktor.server.auth"/>
<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
</tldr>

<link-summary>
The Authentication plugin handles authentication and authorization in Ktor: logging in users, granting access to specific resources, etc.
</link-summary>

Ktor provides the [Authentication](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-authentication/index.html) plugin to handle authentication and authorization. Typical usage scenarios include logging in users, granting access to specific resources, and securely transmitting information between parties. You can also use `Authentication` with [Sessions](sessions.md) to keep a user's information between requests.

## Supported authentication types {id="supported"}
Ktor supports the following authentication and authorization schemes:

### HTTP authentication {id="http-auth"}
HTTP provides a [general framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) for access control and authentication. In Ktor, you can use the following HTTP authentication schemes:
* [Basic](basic.md) - uses `Base64` encoding to provide a username and password. Generally is not recommended if not used in combination with HTTPS.
* [Digest](digest.md) - an authentication method that communicates user credentials in an encrypted form by applying a hash function to the username and password.
* [Bearer](bearer.md) - an authentication scheme that involves security tokens called bearer tokens. 
  The Bearer authentication scheme is used as part of [OAuth](oauth.md) or [JWT](jwt.md), but you can also provide custom logic for authorizing bearer tokens.


### Form-based authentication {id="form-auth"}
[Form-based](form.md) authentication uses a [web form](https://developer.mozilla.org/en-US/docs/Learn/Forms) to collect credential information and authenticate a user.


### JSON Web Tokens (JWT) {id="jwt"}
[JSON Web Token](jwt.md) is an open standard for securely transmitting information between parties as a JSON object. You can use JSON Web Tokens for authorization: when the user is logged in, each request will include a token, allowing the user to access resources that are permitted with that token. In Ktor, you can verify a token and validate the claims contained within it using the `jwt` authentication.


### LDAP {id="ldap"}
[LDAP](ldap.md) is an open and cross-platform protocol used for directory services authentication. Ktor provides the [ldapAuthenticate](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-ldap/io.ktor.server.auth.ldap/ldap-authenticate.html) function to authenticate user credentials against a specified LDAP server.

### OAuth {id="oauth"}
[OAuth](oauth.md) is an open standard for securing access to APIs. The `oauth` provider in Ktor allows you to implement authentication using external providers such as Google, Facebook, Twitter, and so on.

### Session {id="sessions"}
[Sessions](sessions.md) provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, a user that already has an associated session can be authenticated using the `session` provider. Learn how to do this from [](session-auth.md).

### Custom {id="custom"}
Ktor also provides an API for creating [custom plugins](custom_plugins.md), which can be used to implement your own plugin for handling authentication and authorization. 
For example, the `AuthenticationChecked` [hook](custom_plugins.md#call-handling) is executed after authentication credentials are checked, and it allows you to implement authorization: [custom-plugin-authorization](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/custom-plugin-authorization).


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

Note that some authentication providers, such as [JWT](jwt.md) and [LDAP](ldap.md), require additional artifacts.



## Install Authentication {id="install"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure Authentication {id="configure"}
After [installing Authentication](#install), you can configure and use `Authentication` as follows:


### Step 1: Choose an authentication provider {id="choose-provider"}

To use a specific authentication provider ([basic](basic.md), [digest](digest.md), [form](form.md), and so on), you need to call the corresponding function inside the `install` block. For example, to use the `basic` authentication, call the [basic](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/basic.html) function:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    basic {
        // [[[Configure basic authentication|basic.md]]]
    }
}
```
Inside this function, you can [configure](#configure-provider) settings specific to this provider.


### Step 2: Specify a provider name {id="provider-name"}

A function for [using a specific provider](#choose-provider) optionally allows you to specify a provider name. A code sample below installs the [basic](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/basic.html) and [form](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/form.html) providers with the _auth-basic_ and _auth-form_ names, respectively:

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

Each [provider type](#choose-provider) has its own configuration. For instance, the [BasicAuthenticationProvider.Config](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-basic-authentication-provider/-config/index.html) class contains options passed to the [basic](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/basic.html) function. The most important function exposed by this class is [validate](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-basic-authentication-provider/-config/validate.html) that validates a username and password. A code sample below shows how it can look:

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/authbasic/Application.kt" include-lines="9-20"}

To understand how the `validate` function works, we need to introduce two terms:
* A _principal_ is an entity that can be authenticated: a user, a computer, a service, etc. In Ktor, various authentication providers might use different principals. For example, the `basic`, `digest`, and `form` providers authenticate [UserIdPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-id-principal/index.html) while the `jwt` provider verifies [JWTPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/-j-w-t-principal/index.html).
  > You can also create a custom principal by implementing the [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) interface.
  > This might be useful in the following cases:
  > - Mapping the credentials to a custom principal allows you to have additional information about the authenticated principal inside a [route handler](#get-principal).
  > - If you use [session authentication](session-auth.md), a principal might be a data class that stores session data.
* A _credential_ is a set of properties for a server to authenticate a principal: a user/password pair, an API key, and so on. For instance, the `basic` and `form` providers use [UserPasswordCredential](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-password-credential/index.html) to validate a username and password while `jwt` validates [JWTCredential](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/-j-w-t-credential/index.html).

So, the `validate` function checks a specified [Credential](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-credential/index.html) and returns a [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) in the case of successful authentication or `null` if authentication fails.

> To skip authentication based on specific criteria, use [skipWhen](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-authentication-provider/-config/skip-when.html). For example, you can skip `basic` authentication if a [session](sessions.md) already exists:
> ```kotlin
> basic {
>     skipWhen { call -> call.sessions.get<UserSession>() != null }
> }


### Step 4: Protect specific resources {id="authenticate-route"}

The final step is to protect specific resources in our application. You can do this by using the
[authenticate](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/authenticate.html) function. This function accepts two optional parameters:

- A [name of a provider](#provider-name) used to authenticate nested routes.
  The code snippet below uses a provider with the _auth-basic_ name to protect the `/login` and `/orders` routes:
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
- A strategy used to resolve nested authentication providers.
  This strategy is represented by the [AuthenticationStrategy](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-authentication-strategy/index.html) enumeration value.
  For instance, the client should provide authentication data for all providers registered 
  with the `AuthenticationStrategy.Required` strategy.
  In the code snippet below, only a user that passed [session authentication](session-auth.md) 
  can try to access the `/admin` route using basic authentication:
   ```kotlin
   routing {
       authenticate("auth-session", strategy = AuthenticationStrategy.Required) {
           get("/hello") {
               // ...
           }    
           authenticate("auth-basic", strategy = AuthenticationStrategy.Required) {
               get("/admin") {
                   // ...
               }
           }  
       }
   }
   ```
  
  You can find the full example here: [auth-form-session-nested](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-form-session-nested).
   



### Step 5: Get a principal inside a route handler {id="get-principal"}

In the case of successful authentication, you can retrieve an authenticated [Principal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-principal/index.html) inside a route handler using the `call.principal` function. This function accepts a specific principal type returned by the [configured authentication provider](#configure-provider). In a code sample below, `call.principal` is used to obtain `UserIdPrincipal` and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/authbasic/Application.kt" include-lines="21-27"}


If you use [session authentication](session-auth.md), a principal might be a data class that stores session data. So, you need to pass this data class to `call.principal`:

```kotlin
```
{src="snippets/auth-form-session/src/main/kotlin/com/example/Application.kt" include-lines="75-77,80-81"}

In the case of [nested authentication providers](#authenticate-route), 
you can pass a [provider name](#provider-name) to `call.principal` to get a principal for the desired provider.
In the example below, the _auth-session_ value is passed to get a principal for a topmost session provider:

```kotlin
```
{src="snippets/auth-form-session-nested/src/main/kotlin/com/example/Application.kt" include-lines="85,91-93,95-97"}







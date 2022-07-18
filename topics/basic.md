[//]: # (title: Basic authentication)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p>
<b>Code examples</b>: <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/auth-basic">auth-basic</a>, <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/auth-basic-hash-table">auth-basic-hash-table</a>
</p>
</tldr>

The Basic authentication scheme is a part of [HTTP framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) used for access control and authentication. In this scheme, user credentials are transmitted as username/password pairs encoded using Base64.

Ktor allows you to use basic authentication for logging in users and protecting specific [routes](Routing_in_Ktor.md). You can get general information about authentication in Ktor in the [](authentication.md) section.

> Given that basic authentication passes username and password as clear text, you need to use [HTTPS/TLS](ssl.md) to protect sensitive information.

## Add dependencies {id="add_dependencies"}
To enable `basic` authentication, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Basic authentication flow {id="flow"}

The basic authentication flow looks as follows:

1. A client makes a request without the `Authorization` header to a specific [route](Routing_in_Ktor.md) in a server application.
1. A server responds to a client with a `401` (Unauthorized) response status and uses a `WWW-Authenticate` response header to provide information that the basic authentication scheme is used to protect a route. A typical `WWW-Authenticate` header looks like this:
   
   ```
   WWW-Authenticate: Basic realm="Access to the '/' path", charset="UTF-8"
   ```
   {style="block"}
   
   In Ktor, you can specify the realm and charset using corresponding properties when [configuring](#configure-provider) the `basic` authentication provider.

1. Usually a client displays a login dialog where a user can enter credentials. Then, a client makes a request with the `Authorization` header containing a username and password pair encoded using Base64, for example:
   
   ```
   Authorization: Basic amV0YnJhaW5zOmZvb2Jhcg
   ```
   {style="block"}

1. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content.


## Install basic authentication {id="install"}
To install the `basic` authentication provider, call the [basic](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/basic.html) function inside the `install` block:

```kotlin
install(Authentication) {
    basic {
        // Configure basic authentication
    }
}
```

You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure basic authentication {id="configure"}

To get a general idea on how to configure different authentication providers in Ktor, see [](authentication.md#configure). In this section, we'll see on configuration specifics of the `basic` authentication provider. 

### Step 1: Configure a basic provider {id="configure-provider"}

The `basic` authentication provider exposes its settings via the [BasicAuthenticationProvider.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-basic-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `realm` property sets the realm to be passed in `WWW-Authenticate` header.
* The `validate` function validates a username and password.

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="9-20"}
   
The `validate` function checks `UserPasswordCredential` and returns a `UserIdPrincipal` in a case of successful authentication or `null` if authentication fails. 
> You can also use [UserHashedTableAuth](#validate-user-hash) to validate users stored in an in-memory table that keeps usernames and password hashes.

### Step 2: Define authorization scope {id="authenticate-route"}

After configuring the `basic` provider, you can define the authorization for the different resources in our application using the `authenticate` function. In a case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-id-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="21-27"}


## Validate with UserHashedTableAuth {id="validate-user-hash"}

Ktor allows you to use [UserHashedTableAuth](#validate-user-hash) to [validate](#configure-provider) users stored in an in-memory table that keeps usernames and password hashes. This allows you not to compromise user passwords if your data source is leaked.

To use `UserHashedTableAuth` for validating users, follow the steps below:

1. Create a digest function with the specified algorithm and salt provider using the [getDigestFunction](https://api.ktor.io/ktor-utils/io.ktor.util/get-digest-function.html) function:
   
   ```kotlin
   ```
   {src="snippets/auth-basic-hash-table/src/main/kotlin/com/example/Application.kt" lines="9"}

1. Initialize a new instance of `UserHashedTableAuth` and specify the following properties:
   * Provide a table of usernames and hashed passwords using the `table` property.
   * Assign a digest function to the `digester` property.
   
   ```kotlin
   ```
   {src="snippets/auth-basic-hash-table/src/main/kotlin/com/example/Application.kt" lines="10-16"}
   
1. Inside the `validate` function, call the [UserHashedTableAuth.authenticate](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-hashed-table-auth/authenticate.html) function to authenticate a user and return an instance of `UserIdPrincipal` if the credentials are valid:

   ```kotlin
   ```
   {src="snippets/auth-basic-hash-table/src/main/kotlin/com/example/Application.kt" lines="19-26"}
